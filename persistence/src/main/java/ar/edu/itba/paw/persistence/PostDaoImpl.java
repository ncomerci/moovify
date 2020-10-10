package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.InvalidMovieIdException;
import ar.edu.itba.paw.interfaces.persistence.exceptions.InvalidPaginationArgumentException;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;


@Repository
public class PostDaoImpl implements PostDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostDaoImpl.class);

    // Constants with Table Names
    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String MOVIES = TableNames.MOVIES.getTableName();
    private static final String POST_MOVIE = TableNames.POST_MOVIE.getTableName();
    private static final String POSTS_LIKES = TableNames.POSTS_LIKES.getTableName();
    private static final String TAGS = TableNames.TAGS.getTableName();
    private static final String POST_CATEGORY = TableNames.POST_CATEGORY.getTableName();
    private static final String USERS = TableNames.USERS.getTableName();
    private static final String ROLES = TableNames.ROLES.getTableName();
    private static final String USER_ROLE = TableNames.USER_ROLE.getTableName();


    private static final String BASE_POST_SELECT = "SELECT " +
            // Posts Table Columns - Alias: p_column_name
            POSTS + ".post_id p_post_id, " +
            POSTS + ".creation_date p_creation_date, " +
            POSTS + ".title p_title, " +
            POSTS + ".body p_body, " +
            POSTS + ".word_count p_word_count, " +
            POSTS + ".enabled p_enabled";

    private static final String LIKES_SELECT = POSTS_LIKES + ".likes p_likes";

    private static final String CATEGORY_SELECT =
            POST_CATEGORY + ".category_id pc_category_id, " +
            POST_CATEGORY + ".creation_date pc_creation_date, " +
            POST_CATEGORY + ".name pc_name";

    private static final String USER_SELECT =
            USERS + ".user_id u_user_id, " +
            USERS + ".creation_date u_creation_date, " +
            USERS + ".username u_username, " +
            USERS + ".password u_password, " +
            USERS + ".name u_name, " +
            USERS + ".email u_email, " +
            USERS + ".description u_description, " +
            USERS + ".avatar_id u_avatar_id, " +
            USERS + ".enabled u_enabled, " +
            USERS + ".role_id u_role_id, " +
            USERS + ".role u_role";

    private static final String TAGS_SELECT = TAGS + ".tag p_tag";

    private static final String BASE_POST_FROM = "FROM " + POSTS;

    private static final String LIKES_FROM =
            "INNER JOIN " +
                    "(SELECT " + POSTS + ".post_id, COALESCE(SUM( " + POSTS_LIKES + ".value ), 0) likes " +
                    "FROM " + POSTS + " LEFT OUTER JOIN " + POSTS_LIKES + " on " + POSTS + ".post_id = " + POSTS_LIKES + ".post_id" +
                    " GROUP BY " + POSTS + ".post_id ) " + POSTS_LIKES + " ON " + POSTS + ".post_id = " + POSTS_LIKES + ".post_id";

    private static final String CATEGORY_FROM =
            "INNER JOIN " + POST_CATEGORY + " ON " + POSTS + ".category_id = " + POST_CATEGORY + ".category_id";

    private static final String USER_FROM =
            "INNER JOIN (" +
                    "SELECT " +
                        USERS + ".user_id, " +
                        USERS + ".creation_date, " +
                        USERS + ".username, " +
                        USERS + ".password, " +
                        USERS + ".name, " +
                        USERS + ".email, " +
                        USERS + ".description, " +
                        USERS + ".avatar_id, " +
                        USERS + ".enabled, " +
                        ROLES + ".role_id, " +
                        ROLES + ".role " +
                    "FROM " + USERS +
                    " INNER JOIN " + USER_ROLE + " ON " + USERS + ".user_id = " + USER_ROLE + ".user_id " +
                    "INNER JOIN " + ROLES + " ON " + USER_ROLE + ".role_id = " + ROLES + ".role_id " +

            ") " + USERS + " ON " + USERS + ".user_id = " + POSTS + ".user_id";

    private static final String TAGS_FROM =
            "LEFT OUTER JOIN " + TAGS + " ON " + POSTS + ".post_id = " + TAGS + ".post_id";


    private static final ResultSetExtractor<Collection<Post>> POST_ROW_MAPPER = (rs) -> {

        final Map<Long, Post> idToPostMap = new LinkedHashMap<>();
        final Map<Long, Role> idToRoleMap = new HashMap<>();

        long post_id;
        String tag;
        long role_id;

        while(rs.next()) {

            post_id = rs.getLong("p_post_id");

            if (!idToPostMap.containsKey(post_id)) {
                idToPostMap.put(post_id,
                        new Post(
                                post_id, rs.getObject("p_creation_date", LocalDateTime.class),
                                rs.getString("p_title"), rs.getString("p_body"),
                                rs.getInt("p_word_count"),

                                new PostCategory(rs.getLong("pc_category_id"),
                                        rs.getObject("pc_creation_date", LocalDateTime.class),
                                        rs.getString("pc_name")),

                                new User(rs.getLong("u_user_id"), rs.getObject("u_creation_date", LocalDateTime.class),
                                        rs.getString("u_username"), rs.getString("u_password"),
                                        rs.getString("u_name"), rs.getString("u_email"),  rs.getString("u_description"),
                                        rs.getLong("u_avatar_id"), 0,
                                        new HashSet<>(), rs.getBoolean("u_enabled")),

                                // tags
                                new HashSet<>()

                                , rs.getBoolean("p_enabled"),

                                //likes
                                rs.getLong("p_likes")
                        )
                );
            }

            tag = rs.getString("p_tag");

            if (tag != null)
                idToPostMap.get(post_id).getTags().add(tag);

            role_id = rs.getLong("u_role_id");

            if(role_id > 0 && !idToRoleMap.containsKey(role_id))
                idToRoleMap.put(role_id, new Role(role_id, rs.getString("u_role")));

            idToPostMap.get(post_id).getUser().getRoles().add(idToRoleMap.get(role_id));
        }

        return idToPostMap.values();
    };


    private static final EnumMap<SortCriteria,String> sortCriteriaQueryMap = initializeSortCriteriaQuery();

    private static EnumMap<SortCriteria, String> initializeSortCriteriaQuery() {

        final EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, POSTS + ".creation_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, POSTS + ".creation_date");
        sortCriteriaQuery.put(SortCriteria.HOTTEST, POSTS_LIKES + ".likes desc");

        return sortCriteriaQuery;
    }

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
    }
    
    @Override
    public Post register(String title, String body, int wordCount, PostCategory category, User user, Set<String> tags, Set<Long> movies, boolean enabled) {

        Objects.requireNonNull(title);
        Objects.requireNonNull(body);
        Objects.requireNonNull(category);
        Objects.requireNonNull(user);
        Objects.requireNonNull(movies);

        final LocalDateTime creationDate = LocalDateTime.now();

        HashMap<String, Object> map = new HashMap<>();

        map.put("title", title);
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("word_count", wordCount);
        map.put("body", body);
        map.put("category_id", category.getId());
        map.put("user_id", user.getId());
        map.put("enabled", enabled);

        final long postId = postInsert.executeAndReturnKey(map).longValue();

        for(Long movie_id: movies){
            map = new HashMap<>();

            map.put("movie_id", movie_id);
            map.put("post_id", postId);

            try {
                postMoviesInsert.execute(map);
            }
            catch(DataIntegrityViolationException e) {

                if(e.getMessage().contains("post_movie_movie_id_fkey")) {
                    LOGGER.warn("Invalid Movie Id {} Found While Registering Post {}", movie_id, postId);
                    throw new InvalidMovieIdException();
                }

                throw e;
            }
        }

        if(tags != null) {
            for (String tag : tags) {
                map = new HashMap<>();

                map.put("tag", tag);
                map.put("post_id", postId);

                tagsInsert.execute(map);
            }
        }

        final Post post = new Post(postId, creationDate, title, body, wordCount, category, user, tags, enabled, 0);

        LOGGER.debug("Created Post {} with Movies {}", post, movies);

        return post;
    }

    @Override
    public void deletePost(Post post) {

        jdbcTemplate.update("UPDATE " + POSTS + " SET enabled = false WHERE post_id = ?", post.getId());

        LOGGER.info("Post {} was disabled", post.getId());
    }

    @Override
    public void restorePost(Post post) {

        jdbcTemplate.update("UPDATE " + POSTS + " SET enabled = true WHERE post_id = ?", post.getId());

        LOGGER.info("Post {} was restored", post.getId());
    }

    @Override
    public void likePost(Post post, User user, int value) {

        jdbcTemplate.update(
                "INSERT INTO " + POSTS_LIKES + " (post_id, user_id, value) VALUES (?, ?, ?) " +
                        "ON CONFLICT (post_id, user_id) DO UPDATE SET value = ? ", post.getId(), user.getId(), value, value);

        LOGGER.info("User {} liked Post {} with Value {}", user.getId(), post.getId(), value);
    }

    @Override
    public void removeLike(Post post, User user) {

        jdbcTemplate.update( "DELETE FROM " + POSTS_LIKES + " WHERE " + POSTS_LIKES + ".post_id = ? " + " AND "+ POSTS_LIKES + ".user_id = ?", post.getId(), user.getId());

        LOGGER.info("User {} removed like from Post {}", user.getId(), post.getId());
    }

    private Collection<Post> executeQuery(String select, String from, String where, String orderBy, Object[] args) {

        final String query = String.format("%s %s %s %s", select, from, where, orderBy);

        LOGGER.debug("Query executed in PostDaoImpl : {}. Args: {}", query, args);

        if(args != null)
            return jdbcTemplate.query(query, args, POST_ROW_MAPPER);

        else
            return jdbcTemplate.query(query, POST_ROW_MAPPER);
    }

    // For queries where pagination is not necessary (also no order)
    private Collection<Post> buildAndExecuteQuery(String customWhereStatement, Object[] args) {

        final String select = buildSelectStatement();

        final String from = buildFromStatement();

        final Collection<Post> result = executeQuery(select, from, customWhereStatement, "", args);

        LOGGER.debug("Not paginated query executed for {} in PostDaoImpl with result {}", customWhereStatement, result);

        return result;
    }

    private PaginatedCollection<Post> buildAndExecutePaginatedQuery(String customWhereStatement, SortCriteria sortCriteria, int pageNumber, int pageSize, Object[] args) {

        final String select = buildSelectStatement();

        final String from = buildFromStatement();

        final String totalPostCountQuery = String.format(
                "SELECT COUNT(DISTINCT " + POSTS + ".post_id) %s %s", from, customWhereStatement);

        // Execute original query to count total posts in the query
        final int totalPostCount = jdbcTemplate.queryForObject(totalPostCountQuery, args, Integer.class);

        final String orderBy = buildOrderByStatement(sortCriteria);

        final String pagination = buildLimitAndOffsetStatement(pageNumber, pageSize);

        final String newWhere = String.format(
                "WHERE " + POSTS + ".post_id IN ( " +
                        "SELECT AUX.post_id " +
                        "FROM ( " +
                            "SELECT ROW_NUMBER() OVER( %s ) row_num, " + POSTS + ".post_id " +
                            "%s %s ) AUX " +
                        "GROUP BY AUX.post_id " +
                        "ORDER BY MIN(AUX.row_num) " +
                        "%s )",
                orderBy, from, customWhereStatement, pagination);

        final Collection<Post> results = executeQuery(select, from, newWhere, orderBy, args);

        final PaginatedCollection<Post> postPaginatedCollection = new PaginatedCollection<>(results, pageNumber, pageSize, totalPostCount);

        LOGGER.debug("Paginated query executed in PostDaoImpl with result {}", postPaginatedCollection);

        return postPaginatedCollection;
    }

    private String buildSelectStatement() {
       return BASE_POST_SELECT + ", " + LIKES_SELECT + ", " + CATEGORY_SELECT + ", " + USER_SELECT + ", " + TAGS_SELECT;
    }

    private String buildFromStatement() {
        return BASE_POST_FROM + " " + LIKES_FROM + " " + CATEGORY_FROM + " " + USER_FROM + " " + TAGS_FROM;
    }

    private String buildOrderByStatement(SortCriteria sortCriteria) {

        if(!sortCriteriaQueryMap.containsKey(sortCriteria)) {
            LOGGER.error("SortCriteria implementation not found for {} in PostDaoImpl", sortCriteria);
            throw new IllegalArgumentException();
        }

        return "ORDER BY " + sortCriteriaQueryMap.get(sortCriteria);
    }

    private String buildLimitAndOffsetStatement(int pageNumber, int pageSize) {

        if (pageNumber < 0 || pageSize <= 0) {
            LOGGER.error("Invalid pagination argument found in PostDaoImpl. pageSize: {}, pageNumber: {}", pageSize, pageNumber);
            throw new InvalidPaginationArgumentException();
        }

        return String.format("LIMIT %d OFFSET %d", pageSize, pageNumber * pageSize);
    }

    private static final String ENABLED_FILTER = POSTS + ".enabled = true";

    @Override
    public Optional<Post> findPostById(long id){

        LOGGER.info("Find Post By Id {}", id);
        return buildAndExecuteQuery("WHERE " + POSTS + ".post_id = ? AND " + ENABLED_FILTER, new Object[]{ id })
                .stream().findFirst();
    }

    @Override
    public Optional<Post> findDeletedPostById(long id){

        LOGGER.info("Find Deleted Post By Id {}", id);
        return buildAndExecuteQuery("WHERE " + POSTS + ".post_id = ? AND " + POSTS + ".enabled = false",
                new Object[]{ id }).stream().findFirst();
    }

    @Override
    public PaginatedCollection<Post> findPostsByMovie(Movie movie, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Posts By Movie {} Order By {}. Page number {}, Page Size {}", movie.getId(), sortCriteria, pageNumber, pageSize);
        return buildAndExecutePaginatedQuery("WHERE " +
                        POSTS + ".post_id IN ( " +
                        "SELECT " + POST_MOVIE + ".post_id " +
                        "FROM " + POST_MOVIE +
                        " WHERE " + POST_MOVIE + ".movie_id = ?) AND " + ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ movie.getId() });
    }

    @Override
    public PaginatedCollection<Post> findPostsByUser(User user, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Posts By User {} Order By {}. Page number {}, Page Size {}", user.getId(), sortCriteria, pageNumber, pageSize);
        return buildAndExecutePaginatedQuery("WHERE " + POSTS + ".user_id = ? AND " + ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ user.getId() });
    }

    @Override
    public PaginatedCollection<Post> getAllPosts(SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Get All Posts Order By {}. Page number {}, Page Size {}", sortCriteria, pageNumber, pageSize);
        return buildAndExecutePaginatedQuery("WHERE " + ENABLED_FILTER, sortCriteria, pageNumber, pageSize, null);
    }

    @Override
    public PaginatedCollection<Post> getDeletedPosts(SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Get Deleted Posts Order By {}. Page number {}, Page Size {}", sortCriteria, pageNumber, pageSize);
        return buildAndExecutePaginatedQuery("WHERE " + POSTS + ".enabled = false", sortCriteria, pageNumber, pageSize, null);
    }

    // Search Query Statements
    private static final String SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS = "( " +
                    POSTS + ".title ILIKE '%' || ? || '%'" +

                    " OR " + POSTS + ".post_id IN ( " +
                        "SELECT " + TAGS + ".post_id FROM " + TAGS +
                        " WHERE " +  TAGS + ".tag ILIKE '%' || ? || '%' )" +

                    " OR " + POSTS + ".post_id IN ( " +
                        "SELECT " + POST_MOVIE + ".post_id " +
                        " FROM " + POST_MOVIE +
                            " INNER JOIN " + MOVIES + " ON " + POST_MOVIE + ".movie_id = " + MOVIES + ".movie_id " +
                        " WHERE " + MOVIES + ".title ILIKE '%' || ? || '%')" +
                    " )";

    private static final String SEARCH_POSTS_OLDER_THAN = POSTS + ".creation_date >= ?";

    private static final String SEARCH_BY_POST_CATEGORY = POST_CATEGORY + ".name ILIKE ?";


    @Override
    public PaginatedCollection<Post> searchPosts(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Posts By Post Title, Tags and Movie {} Order By {}. Page number {}, Page Size {}", query, sortCriteria, pageNumber, pageSize);
        return buildAndExecutePaginatedQuery(
                "WHERE " + SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS +
                                    " AND " + ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ query, query, query });
    }

    @Override
    public PaginatedCollection<Post> searchDeletedPosts(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Deleted Posts By Post Title, Tags and Movie {} Order By {}. Page number {}, Page Size {}", query, sortCriteria, pageNumber, pageSize);
        return buildAndExecutePaginatedQuery(
                 "WHERE " + SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS +
                                    " AND " + POSTS + ".enabled = false",
                sortCriteria, pageNumber, pageSize, new Object[]{ query, query, query });
    }

    @Override
    public PaginatedCollection<Post> searchPostsByCategory(String query, String category, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Posts By Post Title, Tags, Movie {} And Category {} Order By {}. Page number {}, Page Size {}", query, category, sortCriteria, pageNumber, pageSize);
        return buildAndExecutePaginatedQuery(
                "WHERE " + SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS +
                                    " AND " + SEARCH_BY_POST_CATEGORY +
                                    " AND " + ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ query, query, query, category });
    }

    @Override
    public PaginatedCollection<Post> searchPostsOlderThan(String query, LocalDateTime fromDate, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Posts By Post Title, Tags, Movie {} And Min Age {} Order By {}. Page number {}, Page Size {}", query, fromDate, sortCriteria, pageNumber, pageSize);
        return buildAndExecutePaginatedQuery(
                "WHERE " + SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS +
                                    " AND " + SEARCH_POSTS_OLDER_THAN +
                                    " AND " + ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ query, query, query, Timestamp.valueOf(fromDate) });
    }

    @Override
    public PaginatedCollection<Post> searchPostsByCategoryAndOlderThan(String query, String category, LocalDateTime fromDate, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Posts By Post Title, Tags, Movie {}, Category {} And Min Age {} Order By {}. Page number {}, Page Size {}", query, category, fromDate, sortCriteria, pageNumber, pageSize);
        return buildAndExecutePaginatedQuery(
                "WHERE " + SEARCH_BY_POST_TITLE_MOVIE_TITLE_AND_TAGS +
                                    " AND " + SEARCH_BY_POST_CATEGORY +
                                    " AND " + SEARCH_POSTS_OLDER_THAN +
                                    " AND " + ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ query, query, query, category, Timestamp.valueOf(fromDate) });
    }
}