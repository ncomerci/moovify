package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
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
    private static final String TAGS = TableNames.TAGS.getTableName();
    private static final String COMMENTS = TableNames.COMMENTS.getTableName();

    /*
    *
    * Each SELECT, FROM and MAPPER static variable depend on each other.
    * If one is changed it is necessary to change the others as well to match the design decisions.
    * It is important that they are design only assuming the pre-existence and execution of
    * the BASE_POST static variables (the one Model linked with this Dao). That way, any other
    * can be made optional (currently the case of MOVIES and COMMENTS).
    *
    * Additional requirements of each segment is made explicit below:
    *
    * - ALL:
    *   - All segments must abide to the aliases defined in BASE_POST_SELECT to access Post properties.
    *         Currently p_column_name.
    *
    *   - In BASE_POST_ROW_MAPPER a post_id to Post Map must be maintained for others to use.
    *
    * - TAGS:
    *   - In BASE_POST_ROW_MAPPER, the tags Collection must be a Set to guaranty uniqueness.
    *
    * - MOVIES:
    *   - In BASE_POST_ROW_MAPPER, the movies Collection must be a Set to guaranty uniqueness.
    *
    */

    // Mapper and Select for simple post retrieval.
    private static final String BASE_POST_SELECT = "SELECT " +
            // Posts Table Columns - Alias: p_column_name
            POSTS + ".post_id p_post_id, " +
            POSTS + ".creation_date p_creation_date, " +
            POSTS + ".title p_title, " +
            POSTS + ".body p_body, " + POSTS +
            ".word_count p_word_count, " +
            POSTS + ".email p_email";

    private static final String TAGS_SELECT = TAGS + ".tag p_tag";

    private static final String MOVIES_SELECT =
            MOVIES + ".movie_id m_movie_id, " +
            MOVIES + ".creation_date m_creation_date, " +
            MOVIES + ".title m_title, " +
            MOVIES + ".premier_date m_premier_date";

    private static final String COMMENTS_SELECT =
            COMMENTS + ".comment_id c_comment_id, " +
            "coalesce(" + COMMENTS + ".parent_id, 0) c_parent_id, " +
            COMMENTS + ".post_id c_post_id, " +
            COMMENTS + ".user_email c_user_email, " +
            COMMENTS + ".creation_date c_creation_date, " +
            COMMENTS + ".body c_body";

    private static final String BASE_POST_FROM = "FROM " + POSTS;

    private static final String TAGS_FROM =
            "LEFT OUTER JOIN " + TAGS + " ON " + POSTS + ".post_id = " + TAGS + ".post_id";

    private static final String MOVIES_FROM =
            "LEFT OUTER JOIN (" +
                    " SELECT " + MOVIES + ".movie_id, " + MOVIES + ".creation_date, " +
                    MOVIES + ".title, " + MOVIES + ".premier_date, " + "post_id" +
                    " FROM "+ POST_MOVIE +
                    " INNER JOIN " + MOVIES + " ON " + POST_MOVIE+ ".movie_id = " + MOVIES + ".movie_id" +
                    ") " + MOVIES + " on " + MOVIES + ".post_id = " + POSTS + ".post_id";

    private static final String COMMENTS_FROM =
            "LEFT OUTER JOIN " + COMMENTS + " ON " + POSTS + ".post_id = " + COMMENTS + ".post_id";


    private static final ResultSetConsumer<Map<Long, Post>> BASE_POST_ROW_MAPPER = (rs, idToPostMap) -> {
        final long post_id = rs.getLong("p_post_id");

        if (!idToPostMap.containsKey(post_id)) {
            idToPostMap.put(post_id,
                    new Post(
                            post_id, rs.getObject("p_creation_date", LocalDateTime.class),
                            rs.getString("p_title"), rs.getString("p_body"),
                            rs.getInt("p_word_count"), rs.getString("p_email"),
                            new HashSet<>(), new HashSet<>(), new ArrayList<>()
                    )
            );
        }
    };

    private static final ResultSetConsumer<Map<Long, Post>> TAGS_ROW_MAPPER = (rs, idToPostMap) -> {
        final long post_id = rs.getLong("p_post_id");
        final String tag = rs.getString("p_tag");

        if (tag != null)
            idToPostMap.get(post_id).getTags().add(tag);
    };

    private static final ResultSetBiConsumer<Map<Long, Post>, Map<Long, Movie>> MOVIES_ROW_MAPPER =
            (rs, idToPostMap, idToMovieMap) -> {

        final long post_id = rs.getLong("p_post_id");
        final long movie_id = rs.getLong("m_movie_id");

        // If movies is not null. (rs.getLong returns 0 on null)
        if(movie_id != 0) {

            if(!idToMovieMap.containsKey(movie_id)) {
                idToMovieMap.put(movie_id,
                        new Movie(
                                movie_id, rs.getObject("m_creation_date", LocalDateTime.class),
                                rs.getString("m_title"), rs.getObject("m_premier_date", LocalDate.class)
                        ));
            }

            // If the Post already had the Movie, it won't get added because the Collection is a Set
            idToPostMap.get(post_id).getMovies().add(idToMovieMap.get(movie_id));
        }
    };

    private static final ResultSetTriConsumer<Map<Long, Post>, Map<Long, Comment>, Map<Long, Collection<Comment>>> COMMENTS_ROW_MAPPER =
            (rs, idToPostMap, idToCommentMap, childrenWithoutParentMap) -> {

        final long comment_id = rs.getLong("c_comment_id");
        Comment newComment;

        // Returns 0 on null
        if(comment_id != 0 && !idToCommentMap.containsKey(comment_id)) {

            newComment = new Comment(comment_id,
                    rs.getObject("c_creation_date", LocalDateTime.class),
                    rs.getLong("c_post_id"), rs.getLong("c_parent_id"), new ArrayList<>(),
                    rs.getString("c_body"), rs.getString("c_user_email"));

            idToCommentMap.put(comment_id, newComment);

            // Incorporate all children that appeared before currentComment
            if(childrenWithoutParentMap.containsKey(comment_id)) {
                newComment.getChildren().addAll(childrenWithoutParentMap.get(comment_id));

                // Mapping is no longer necessary
                childrenWithoutParentMap.remove(comment_id);
            }

            // Comment is root
            if (newComment.getParentId() == 0)
                idToPostMap.get(newComment.getPostId()).getComments().add(newComment);

            else {
                // If parent doesn't exist yet
                if(!idToCommentMap.containsKey(newComment.getParentId())) {

                    // Initialize Collection inside Map if necessary
                    if(!childrenWithoutParentMap.containsKey(newComment.getParentId()))
                        childrenWithoutParentMap.put(newComment.getParentId(), new ArrayList<>());

                    // Add children to Parent Children Buffer
                    childrenWithoutParentMap.get(newComment.getParentId()).add(newComment);
                }

                // Parent exists -> Add to parent
                else
                    idToCommentMap.get(newComment.getParentId()).getChildren().add(newComment);
            }
        }
    };

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert postInsert;
    private final SimpleJdbcInsert postMoviesInsert;
    private final SimpleJdbcInsert tagsInsert;

    @Autowired
    public PostDaoImpl(final DataSource ds){

        jdbcTemplate = new JdbcTemplate(ds);

        postInsert = new SimpleJdbcInsert(ds)
                .withTableName(POSTS)
                .usingGeneratedKeyColumns("post_id");

        postMoviesInsert = new SimpleJdbcInsert(ds)
                .withTableName(POST_MOVIE);

        tagsInsert = new SimpleJdbcInsert(ds)
                .withTableName(TAGS);

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

        jdbcTemplate.execute( "CREATE TABLE IF NOT EXISTS " + TAGS + " (" +
                "post_id integer," +
                "tag VARCHAR(30) NOT NULL," +
                "PRIMARY KEY (post_id, tag)," +
                "FOREIGN KEY (post_id) REFERENCES " + POSTS + " (post_id))");
    }


    @Override
    public Post register(String title, String email, String body, Collection<String> tags, Set<Long> movies) {

        body = body.trim();
        LocalDateTime creationDate = LocalDateTime.now();
        int wordCount = body.split("\\s+").length;

        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("email", email);
        map.put("word_count", wordCount);
        map.put("body", body);
        map.put("tags", tags);

        final long postId = postInsert.executeAndReturnKey(map).longValue();

        for(Long movie_id: movies){
            map = new HashMap<>();
            map.put("movie_id", movie_id);
            map.put("post_id", postId);
            postMoviesInsert.execute(map);
        }

        for(String tag: tags){
            map = new HashMap<>();
            map.put("tag", tag);
            map.put("post_id", postId);
            tagsInsert.execute(map);
        }

        return new Post(postId, creationDate, title, body, wordCount, email, tags, null, null);
    }

    // This method abstract the logic needed to perform select queries with or without movies.
    private Collection<Post> findPostsBy(String queryAfterFrom, Object[] args, boolean withMovies, boolean withComments){

        final String select = BASE_POST_SELECT
                + ", " + TAGS_SELECT
                + (withMovies? ", " + MOVIES_SELECT : "")
                + (withComments? ", " + COMMENTS_SELECT : "");

        final String from = BASE_POST_FROM
               + " " + TAGS_FROM
                + (withMovies? " " + MOVIES_FROM : "")
                + (withComments? " " + COMMENTS_FROM : "");

        final String query = select + " " + from + " " + queryAfterFrom;

        final ResultSetExtractor<Collection<Post>> rowMapper = getPostRowMapper(withMovies, withComments);


        if(args != null)
            return jdbcTemplate.query(query, args, rowMapper);

        else
            return jdbcTemplate.query(query, rowMapper);
    }

    private Collection<Post> findPostsBy(String queryAfterFrom, boolean withMovies, boolean withComments){
        return findPostsBy(queryAfterFrom, null, withMovies, withComments);
    }

    private ResultSetExtractor<Collection<Post>> getPostRowMapper(final boolean withMovies, final boolean withComments) {
        // Option 1
/*
        if(withMovies && withComments)
            return POST_ROW_MAPPER_WITH_MOVIES_WITH_COMMENTS;

        else if(withMovies)
            return POST_ROW_MAPPER_WITH_MOVIES;

        else if(withComments)
            return POST_ROW_MAPPER_WITH_COMMENTS;

        else
            return POST_ROW_MAPPER;
 */

        // TODO: Take a final decision on options
        // Option 2 (current approach)

        return (rs) -> {
            final Map<Long, Post> idToPostMap = new HashMap<>();
            final Map<Long, Movie> idToMovieMap = new HashMap<>();
            final Map<Long, Comment> idToCommentMap = new HashMap<>();
            final Map<Long, Collection<Comment>> childrenWithoutParentMap = new HashMap<>();

            while(rs.next()) {

                BASE_POST_ROW_MAPPER.accept(rs, idToPostMap);

                TAGS_ROW_MAPPER.accept(rs, idToPostMap);

                if (withMovies)
                    MOVIES_ROW_MAPPER.accept(rs, idToPostMap, idToMovieMap);

                if (withComments)
                    COMMENTS_ROW_MAPPER.accept(rs, idToPostMap, idToCommentMap, childrenWithoutParentMap);

            }

            return idToPostMap.values();
        };

        // Option 3: Dynamically generate all ResultSetExtractor options and store it statically. Best of both worlds.
    }

    @Override
    public Optional<Post> findPostById(long id, boolean withMovies, boolean withComments){
        return findPostsBy(
                " WHERE " + POSTS + ".post_id = ?", new Object[]{ id }, withMovies, withComments)
                .stream().findFirst();
    }

    @Override
    public Collection<Post> findPostsByTitle(String title, boolean withMovies, boolean withComments) {
        return findPostsBy(
                " WHERE " + POSTS + ".title ILIKE '%' || ? || '%' " +
                        "ORDER BY " + POSTS + ".creation_date", new Object[] { title }, withMovies, withComments);
    }

    @Override
    public Collection<Post> findPostsByMovieId(long movie_id, boolean withMovies, boolean withComments) {
        return findPostsBy(
                " WHERE " + POSTS + ".post_id IN ( " +
                        "SELECT " + POST_MOVIE + ".post_id " +
                        "FROM " + POST_MOVIE +
                        " WHERE " + POST_MOVIE + ".movie_id = ?) " +
                        "ORDER BY " + POSTS + ".creation_date", new Object[] { movie_id }, withMovies, withComments);
    }

    @Override
    public Collection<Post> findPostsByMovieTitle(String movie_title, boolean withMovies, boolean withComments) {
        return findPostsBy(
                " WHERE " + POSTS + ".post_id IN ( " +
                        "SELECT " + POSTS + ".post_id " +
                        "FROM " + POST_MOVIE +
                        " INNER JOIN " + MOVIES + " ON " + POST_MOVIE + ".movie_id = " + MOVIES + ".movie_id " +
                        "WHERE " + POSTS + ".title ILIKE '%' || ? || '%') " +
                        "ORDER BY " + POSTS + ".creation_date", new Object[] { movie_title }, withMovies, withComments);
    }

    @Override
    public Collection<Post> getAllPosts(boolean withMovies, boolean withComments) {
        return findPostsBy(" ORDER BY " + POSTS + ".creation_date", withMovies, withComments);
    }

    @Override
    public Collection<Post> findPostsByPostAndMovieTitle(String title, boolean withMovies, boolean withComments) {
        return findPostsBy(
                " WHERE " + POSTS + ".title ILIKE '%' || ? || '%'" +
                "OR " + POSTS + ".post_id in ( " +
                "SELECT " + POST_MOVIE + ".post_id " +
                "FROM " + POST_MOVIE +
                " INNER JOIN " + MOVIES + " ON " + POST_MOVIE + ".movie_id = " + MOVIES + ".movie_id " +
                "WHERE " + MOVIES + ".title ILIKE '%' || ? || '%') " +
                "ORDER BY " + POSTS + ".creation_date", new Object[] {title, title}, withMovies, withComments);
    }
}

/* TODO: Keeping code for back up and reference. Remove after testing.

    private static final ResultSetExtractor<Collection<Post>> POST_ROW_MAPPER_WITH_MOVIES_WITH_COMMENTS = (rs) -> {
        Map<Long, Post> resultMap = new HashMap<>();
        Map<Long, Map<Long, Movie>> movieMap = new HashMap<>();
        Map<Long, Comment> idToCommentMap = new HashMap<>();

        long post_id;
        Comment currentComment;
        long comment_id;

        while(rs.next()){

            // POST SECTION
            post_id = rs.getLong("p_post_id");

            if(!resultMap.containsKey(post_id)){
                resultMap.put(post_id,
                        new Post(
                                post_id, rs.getObject("p_creation_date", LocalDateTime.class),
                                rs.getString("p_title"), rs.getString("p_body"),
                                rs.getInt("p_word_count"), rs.getString("p_email"),
                                new HashSet<>(), new ArrayList<>(), new ArrayList<>()
                        )
                );
            }

            // MOVIE SECTION
            long movie_id = rs.getLong("m_movie_id");

            // If movies is not null. (Returns 0 on null)
            if(movie_id != 0) {
                if (!movieMap.containsKey(post_id))
                    movieMap.put(post_id, new HashMap<>());

                if (!movieMap.get(post_id).containsKey(movie_id))
                    movieMap.get(post_id).put(movie_id,
                            new Movie( movie_id, rs.getObject("m_creation_date", LocalDateTime.class),
                            rs.getString("m_title"), rs.getObject("m_premier_date", LocalDate.class)));
            }

            // TAGS SECTION
            String tag = rs.getString("p_tag");

            if (tag != null)
                resultMap.get(post_id).getTags().add(tag);

            // COMMENTS SECTION
            comment_id = rs.getLong("c_comment_id");

            // Return 0 on null
            if(comment_id != 0 && !idToCommentMap.containsKey(comment_id)) {


                currentComment = new Comment(rs.getLong("c_comment_id"),
                        rs.getObject("c_creation_date", LocalDateTime.class),
                        rs.getLong("c_post_id"), rs.getLong("c_parent_id"), new ArrayList<>(),
                        rs.getString("c_body"), rs.getString("c_user_email"));

                idToCommentMap.put(comment_id, currentComment);

                // Comment is root
                if (currentComment.getParentId() == 0)
                    resultMap.get(currentComment.getPostId()).getComments().add(currentComment);

                else
                    idToCommentMap.get(currentComment.getParentId()).getChildren().add(currentComment);
            }

        }

        Collection<Post> posts = resultMap.values();

        for (Post p: posts) {
            if(movieMap.containsKey(p.getId()))
                p.getMovies().addAll(movieMap.get(p.getId()).values());
        }

        return posts;
    };
 */