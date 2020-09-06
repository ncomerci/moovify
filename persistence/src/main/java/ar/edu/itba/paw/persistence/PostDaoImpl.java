package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Repository
public class PostDaoImpl implements PostDao {

    // Constants with Table Names
    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String MOVIES = TableNames.MOVIES.getTableName();
    private static final String POST_MOVIE = TableNames.POST_MOVIE.getTableName();
    private static final String COMMENTS = TableNames.COMMENTS.getTableName();


    // Use each MAPPER with it's corresponding SELECT Macro. Update them together.

    // Mapper and Select for simple post retrieval.
    private static final String SELECT_POSTS = "SELECT * FROM " + POSTS;

    private static final RowMapper<Post> POST_ROW_MAPPER = (rs, rowNum) ->
            new Post(rs.getLong("post_id"), rs.getObject("creation_date", LocalDateTime.class),
                    rs.getString("title"), rs.getString("body"),
                    rs.getInt("word_count"), rs.getString("email"), null, null);


    // Mapper and Select for post retrievals which include movies info.
    // DO NOT USE ALIASES IN QUERIES INVOLVING THIS MAPPER
    private static final String SELECT_POSTS_WITH_MOVIES = "SELECT " +
            // Posts Table Columns - Alias: p_column_name
            POSTS + ".post_id p_post_id, " + POSTS + ".creation_date p_creation_date, " + POSTS + ".title p_title, " +
            POSTS + ".body p_body, " + POSTS + ".word_count p_word_count, " + POSTS + ".email p_email, " +

            // Movies Table Columns - Alias: m_column_name
            MOVIES + ".movie_id m_movie_id, " + MOVIES + ".creation_date m_creation_date, " + MOVIES + ".title m_title, " +
            MOVIES + ".premier_date m_premier_date " +

            // Outer Joins between posts - post_movie - movies Tables
            "FROM " +
            POSTS + " LEFT OUTER JOIN " + POST_MOVIE + " ON " + POSTS + ".post_id = " + POST_MOVIE + ".post_id " +
            "LEFT OUTER JOIN " + MOVIES + " ON " + MOVIES + ".movie_id = " + POST_MOVIE + ".movie_id ";

    private static final ResultSetExtractor<Collection<Post>> POST_ROW_MAPPER_WITH_MOVIES = (rs) -> {
        Map<Long, Post> resultMap = new HashMap<>();
        long post_id;

        while(rs.next()){
            post_id = rs.getLong("p_post_id");

            if(!resultMap.containsKey(post_id)){
                resultMap.put(post_id,
                        new Post(
                                post_id, rs.getObject("p_creation_date", LocalDateTime.class),
                                rs.getString("p_title"), rs.getString("p_body"),
                                rs.getInt("p_word_count"), rs.getString("p_email"),
                                new ArrayList<>(), null
                        )
                );
            }

            // If movies is not null. (Returns 0 on null)
            if(rs.getLong("m_movie_id") != 0) {

                resultMap.get(post_id).getMovies().add(
                        new Movie(
                                rs.getLong("m_movie_id"), rs.getObject("m_creation_date", LocalDateTime.class),
                                rs.getString("m_title"), rs.getObject("m_premier_date", LocalDate.class)
                        )
                );
            }
        }

        return resultMap.values();
    };


    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert postInsert;
    private final SimpleJdbcInsert postMoviesInsert;

    @Autowired
    public PostDaoImpl(final DataSource ds){

        jdbcTemplate = new JdbcTemplate(ds);

        postInsert = new SimpleJdbcInsert(ds)
                .withTableName(POSTS)
                .usingGeneratedKeyColumns("post_id");

        postMoviesInsert = new SimpleJdbcInsert(ds)
                .withTableName(POST_MOVIE);

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + POSTS + " (" +
                        "post_id SERIAL PRIMARY KEY," +
                        "creation_date TIMESTAMP NOT NULL," +
                        "title VARCHAR(50) NOT NULL," +
                        "email VARCHAR(40) NOT NULL," +
                        "word_count INTEGER," +
                        "body VARCHAR )"
        );

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + POST_MOVIE + " (" +
                "post_id integer," +
                "movie_id integer," +
                "PRIMARY KEY (post_id, movie_id)," +
                "FOREIGN KEY (post_id) REFERENCES " + POSTS + " (post_id)," +
                "FOREIGN KEY (movie_id) REFERENCES " + MOVIES + " (movie_id))"
        );
    }


    @Override
    public Post register(String title, String email, String body, Set<Long> movies) {

        body = body.trim();
        LocalDateTime creationDate = LocalDateTime.now();
        int wordCount = body.split("\\s+").length;

        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("email", email);
        map.put("word_count", wordCount);
        map.put("body", body);

        final long postId = postInsert.executeAndReturnKey(map).longValue();

        for(Long movie_id: movies){
            map = new HashMap<>();
            map.put("movie_id", movie_id);
            map.put("post_id", postId);
            postMoviesInsert.execute(map);
        }

        return new Post(postId, creationDate, title, body, wordCount, email, null, null);
    }

    // This two methods abstract the logic needed to perform select queries with or without movies.
    private Collection<Post> findPostsBy(String queryAfterFrom, Object[] args, boolean withMovies){
        if(withMovies)
            return jdbcTemplate.query(SELECT_POSTS_WITH_MOVIES + " " + queryAfterFrom, args, POST_ROW_MAPPER_WITH_MOVIES);

        else
            return jdbcTemplate.query(SELECT_POSTS + " " + queryAfterFrom, args, POST_ROW_MAPPER);
    }

    private Collection<Post> findPostsBy(String queryAfterFrom, boolean withMovies){
        if(withMovies)
            return jdbcTemplate.query(SELECT_POSTS_WITH_MOVIES + " " + queryAfterFrom, POST_ROW_MAPPER_WITH_MOVIES);

        else
            return jdbcTemplate.query(SELECT_POSTS + " " + queryAfterFrom, POST_ROW_MAPPER);
    }

    @Override
    public Optional<Post> findPostById(long id, boolean withMovies){
        return findPostsBy(
                " WHERE " + POSTS + ".post_id = ?", new Object[]{ id }, withMovies).stream().findFirst();
    }

    @Override
    public Collection<Post> findPostsByTitle(String title, boolean withMovies) {
        return findPostsBy(
                " WHERE " + POSTS + ".title ILIKE '%' || ? || '%' " +
                "ORDER BY " + POSTS + ".creation_date", new Object[] { title }, withMovies);
    }

    @Override
    public Collection<Post> findPostsByMovieId(long movie_id, boolean withMovies) {
        return findPostsBy(
                " WHERE " + POSTS + ".post_id IN ( " +
                "SELECT " + POST_MOVIE + ".post_id " +
                "FROM " + POST_MOVIE +
                " WHERE " + POST_MOVIE + ".movie_id = ?) " +
                "ORDER BY " + POSTS + ".creation_date", new Object[] { movie_id }, withMovies);
    }

    @Override
    public Collection<Post> findPostsByMovieTitle(String movie_title, boolean withMovies) {
        return findPostsBy(
                " WHERE " + POSTS + ".post_id IN ( " +
                "SELECT " + POSTS + ".post_id " +
                "FROM " + POST_MOVIE +
                " INNER JOIN " + MOVIES + " ON " + POST_MOVIE + ".movie_id = " + MOVIES + ".movie_id " +
                "WHERE " + POSTS + ".title ILIKE '%' || ? || '%') " +
                "ORDER BY " + POSTS + ".creation_date", new Object[] { movie_title }, withMovies);
    }

    @Override
    public Collection<Post> getAllPosts(boolean withMovies) {
        return findPostsBy(" ORDER BY " + POSTS + ".creation_date", withMovies);
    }

    @Override
    public Collection<Post> findPostsByPostAndMovieTitle(String title, boolean withMovies) {
        return findPostsBy(
                " WHERE " + POSTS + ".title ILIKE '%' || ? || '%'" +
                "OR " + POSTS + ".post_id in ( " +
                "SELECT " + POST_MOVIE + ".post_id " +
                "FROM " + POST_MOVIE +
                " INNER JOIN " + MOVIES + " ON " + POST_MOVIE + ".movie_id = " + MOVIES + ".movie_id " +
                "WHERE " + MOVIES + ".title ILIKE '%' || ? || '%') " +
                "ORDER BY " + POSTS + ".creation_date", new Object[] {title, title}, withMovies);
    }

}

// Query Back Up For Now... TODO: Remove when we test enough
/*

@Override
    public Optional<Post> findById(long id){
        return jdbcTemplate.query("SELECT * FROM " + POSTS + " WHERE post_id = ?",
                new Object[]{ id }, POST_ROW_MAPPER)
                .stream().findFirst();
    }

@Override
    public Collection<Post> findPostsByTitle(String title) {
        return jdbcTemplate.query("SELECT * FROM " + POSTS + " WHERE title ILIKE '%' || ? || '%' ORDER BY creation_date", new Object[] { title }, POST_ROW_MAPPER);
    }

@Override
    public Collection<Post> findPostsByMovieId(long movie_id) {
        return jdbcTemplate.query("SELECT * FROM " + POSTS + " WHERE post_id in " +
        " ( SELECT post_id FROM " + POST_MOVIE + " WHERE movie_id = ? ORDER BY creation_date ) ", new Object[] { movie_id }, POST_ROW_MAPPER);
    }

@Override
    public Collection<Post> findPostsByMovieTitle(String movie_title) {
        return jdbcTemplate.query("SELECT * FROM " + POSTS + " WHERE post_id in " +
                " (SELECT post_id FROM " + POST_MOVIE + " INNER JOIN " + MOVIES + " ON " + POST_MOVIE + ".movie_id = " + MOVIES + ".movie_id "
                + " WHERE title ILIKE ? ) ORDER BY creation_date ", new Object[] { '%' + movie_title + '%' }, POST_ROW_MAPPER);
    }

@Override
    public Collection<Post> getAllPosts() {
        return jdbcTemplate.query("SELECT * FROM " + POSTS + " ORDER BY creation_date", POST_ROW_MAPPER);
    }

@Override
    public Collection<Post> findPostsByPostAndMovieTitle(String searchParam) {
        return jdbcTemplate.query("SELECT * FROM " + POSTS + " WHERE title LIKE '%' || ? || '%' OR post_id in " +
                " (SELECT post_id FROM " + POST_MOVIE + " as pm INNER JOIN " + MOVIES + " as m on pm.movie_id = m.movie_id "
                + " WHERE title LIKE '%' || ? || '%' ) ORDER BY creation_date ", new Object[] { searchParam , searchParam }, POST_ROW_MAPPER);

    }
 */
