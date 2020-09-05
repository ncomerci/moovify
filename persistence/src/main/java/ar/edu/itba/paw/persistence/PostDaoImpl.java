package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;


@Repository
public class PostDaoImpl implements PostDao {

    @Autowired
    private MovieDao movieDao;

    // TODO: Sacamos el static para poder acceder al movie dao cuando necesitamos el set de movies, esta legal ¡?¡?
    private final RowMapper<Post> POST_ROW_MAPPER = (rs, rowNum) ->
            new Post(rs.getLong("post_id"), rs.getObject("creation_date", LocalDateTime.class),
                    rs.getString("title"), rs.getString("body"),
                    rs.getInt("word_count"), rs.getString("email"),
                    movieDao.getMoviesByPost(rs.getLong("post_id")));

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert postInsert;
    private final SimpleJdbcInsert postMoviesInsert;

    @Autowired
    public PostDaoImpl(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        postInsert = new SimpleJdbcInsert(ds)
                .withTableName(TableNames.POSTS.getTableName())
                .usingGeneratedKeyColumns("post_id");
        postMoviesInsert = new SimpleJdbcInsert(ds)
                .withTableName(TableNames.POST_MOVIE.getTableName());

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + TableNames.POSTS.getTableName() + " (" +
                        "post_id SERIAL PRIMARY KEY," +
                        "creation_date TIMESTAMP NOT NULL," +
                        "title VARCHAR(50) NOT NULL," +
                        "email VARCHAR(40) NOT NULL," +
                        "word_count INTEGER," +
                        "body VARCHAR )"
        );

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + TableNames.POST_MOVIE.getTableName() + " (" +
                "post_id integer," +
                "movie_id integer," +
                "PRIMARY KEY (post_id, movie_id)," +
                "FOREIGN KEY (post_id) REFERENCES " + TableNames.POSTS.getTableName() + " (post_id)," +
                "FOREIGN KEY (movie_id) REFERENCES " + TableNames.MOVIES.getTableName() + " (movie_id))"
        );
    }

    @Override
    public Optional<Post> findById(long id){
        List<Post> results = jdbcTemplate.query("SELECT * FROM " + TableNames.POSTS.getTableName() + " WHERE post_id = ?", new Object[]{ id }, POST_ROW_MAPPER);

        return results.stream().findFirst();
    }

    @Override
    public Set<Post> findPostsByTitle(String title) {
        List<Post> result = jdbcTemplate.query("SELECT * FROM " + TableNames.POSTS.getTableName() + " WHERE title LIKE ? ORDER BY creation_date", new Object[] { '%' +title+ '%' }, POST_ROW_MAPPER );

        return new HashSet<>(result);
    }
    @Override
    public Set<Post> findPostsByMovieId(long movie_id) {
        List<Post> result = jdbcTemplate.query("SELECT * FROM " + TableNames.POSTS.getTableName() + " WHERE post_id in " +
        " ( SELECT post_id FROM " + TableNames.POST_MOVIE.getTableName() + " WHERE movie_id = ? ORDER BY creation_date ) ", new Object[] { movie_id }, POST_ROW_MAPPER );

        return new HashSet<>(result);
    }

    @Override
    public Set<Post> findPostsByMovieTitle(String movie_title) {
        List<Post> result = jdbcTemplate.query("SELECT * FROM " + TableNames.POSTS.getTableName() + " WHERE post_id in " +
                " (SELECT post_id FROM " + TableNames.POST_MOVIE.getTableName() + " as pm INNER JOIN " + TableNames.MOVIES.getTableName() + " as m on pm.movie_id = m.movie_id "
                + " WHERE title LIKE ? ) ORDER BY creation_date ", new Object[] { '%' + movie_title + '%' }, POST_ROW_MAPPER );

        return new HashSet<>(result);
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

        return new Post(postId, creationDate, title, body, wordCount, email, movieDao.getMoviesByPost(postId));
    }

//  TODO refactorear el nombre para que indique que son ordenados
    @Override
    public Set<Post> getAllPosts() {
        Set<Post> result = new TreeSet<>(Comparator.comparing(Post::getCreationDate).reversed());

        result.addAll(jdbcTemplate.query("SELECT * FROM posts ORDER BY creation_date", POST_ROW_MAPPER));

        return result;
    }

}
