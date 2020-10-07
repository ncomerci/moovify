package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
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
                    "(SELECT " + POSTS + ".post_id, COUNT( " + POSTS_LIKES + ".user_id ) likes " +
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
                                        rs.getLong("u_avatar_id"),
                                        new HashSet<>(), rs.getBoolean("u_enabled"), null),

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

    private enum FilterCriteria {
        BY_POST_TITLE_MOVIE_TITLE_AND_TAGS(
                "( " +
                        POSTS + ".title ILIKE '%' || ? || '%'" +
                        " OR " + POSTS + ".post_id IN (" +
                        " SELECT " + TAGS + ".post_id FROM " + TAGS +
                        " WHERE " +  TAGS + ".tag ILIKE '%' || ? || '%' )" +
                        " OR " + POSTS + ".post_id IN ( " +
                        "SELECT " + POST_MOVIE + ".post_id " +
                        " FROM " + POST_MOVIE +
                        " INNER JOIN " + MOVIES + " ON " + POST_MOVIE + ".movie_id = " + MOVIES + ".movie_id " +
                        " WHERE " + MOVIES + ".title ILIKE '%' || ? || '%')" +
                        " )"
        ),

        POSTS_OLDER_THAN(
                POSTS + ".creation_date >= ?"
        ),

        BY_POST_CATEGORY(
                POST_CATEGORY + ".name ILIKE ?"
        ),

        ENABLED(POSTS + ".enabled = true"),

        NOT_ENABLED(POSTS + ".enabled = false");

        public final String filterQuery;

        FilterCriteria(String filterQuery) {
            this.filterQuery = filterQuery;
        }
    }

    private static final EnumMap<SortCriteria,String> sortCriteriaQueryMap = initializeSortCriteriaQuery();

    private static EnumMap<SortCriteria, String> initializeSortCriteriaQuery() {

        EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, POSTS + ".creation_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, POSTS + ".creation_date");
        sortCriteriaQuery.put(SortCriteria.HOTTEST, POSTS_LIKES + ".likes desc");

        return sortCriteriaQuery;
    }


    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert postInsert;
    private final SimpleJdbcInsert postMoviesInsert;
    private final SimpleJdbcInsert tagsInsert;
    private final SimpleJdbcInsert postLikesInsert;

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

        postLikesInsert = new SimpleJdbcInsert(ds)
                .withTableName(POSTS_LIKES);
    }
    
    @Override
    public long register(String title, String body, int wordCount, long categoryId, long userId, Set<String> tags, Set<Long> movies, boolean enabled) {

        Objects.requireNonNull(title);
        Objects.requireNonNull(body);
        Objects.requireNonNull(movies);

        LocalDateTime creationDate = LocalDateTime.now();

        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("word_count", wordCount);
        map.put("body", body);
        map.put("category_id", categoryId);
        map.put("user_id", userId);
        map.put("enabled", enabled);

        final long postId = postInsert.executeAndReturnKey(map).longValue();

        for(Long movie_id: movies){
            map = new HashMap<>();
            map.put("movie_id", movie_id);
            map.put("post_id", postId);
            postMoviesInsert.execute(map);
        }

        if(tags != null) {
            for (String tag : tags) {
                map = new HashMap<>();
                map.put("tag", tag);
                map.put("post_id", postId);
                tagsInsert.execute(map);
            }
        }

        return postId;
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("UPDATE " + POSTS + " SET enabled = false WHERE post_id = ?",  id);
    }

    @Override
    public void restore(long id) {
        jdbcTemplate.update("UPDATE " + POSTS + " SET enabled = true WHERE post_id = ?",  id);
    }

    @Override
    public void likePost(long post_id, long user_id) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("post_id", post_id);
        map.put("user_id", user_id);

        postLikesInsert.execute(map);
    }

    @Override
    public void removeLike(long post_id, long user_id) {

        jdbcTemplate.update( "DELETE FROM " + POSTS_LIKES + " WHERE " + POSTS_LIKES + ".post_id = ? " + " AND "+ POSTS_LIKES + ".user_id = ?", post_id, user_id );
    }

    private Collection<Post> executeQuery(String select, String from, String where, String orderBy, Object[] args) {

//        TODO: NICO NO SE PUEDE PONER ACA
//                final String whereEnabledFilter = customWhereStatement == null || customWhereStatement.length() < 3 ?
//                "WHERE " + POSTS + ".enabled = true" : "AND " + POSTS + ".enabled = true";
//
//        final String query = select + " " + from + " " + customWhereStatement + " " + whereEnabledFilter + " " + customOrderByStatement;
        final String query = select + " " + from + " " + where + " " + orderBy;

        if(args != null)
            return jdbcTemplate.query(query, args, POST_ROW_MAPPER);

        else
            return jdbcTemplate.query(query, POST_ROW_MAPPER);
    }

    // For queries where pagination is not necessary (also no order)
    private Collection<Post> buildAndExecuteQuery(String customWhereStatement, Object[] args) {

        final String select = BASE_POST_SELECT + ", " + LIKES_SELECT + ", " + CATEGORY_SELECT + ", " + USER_SELECT + ", " + TAGS_SELECT;

        final String from = BASE_POST_FROM + " " + LIKES_FROM + " " + CATEGORY_FROM + " " + USER_FROM + " " + TAGS_FROM;

        return executeQuery(select, from, customWhereStatement, "", args);
    }

    private PaginatedCollection<Post> buildAndExecutePaginatedQuery(String customWhereStatement, SortCriteria sortCriteria, int pageNumber, int pageSize, Object[] args) {

        final String select = BASE_POST_SELECT + ", " + LIKES_SELECT + ", " + CATEGORY_SELECT + ", " + USER_SELECT + ", " + TAGS_SELECT;

        final String from = BASE_POST_FROM + " " + LIKES_FROM + " " + CATEGORY_FROM + " " + USER_FROM + " " + TAGS_FROM;

        // Execute original query to count total posts in the query
        final int totalPostCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT " + POSTS + ".post_id) " + from + " " + customWhereStatement, args, Integer.class);

        final String orderBy = buildOrderByStatement(sortCriteria);

        final String pagination = buildLimitAndOffsetStatement(pageNumber, pageSize);

        final String newWhere = "WHERE " + POSTS + ".post_id IN (SELECT " + POSTS + ".post_id FROM " + POSTS + " WHERE " + POSTS + ".post_id IN (" +
                                    "SELECT " + POSTS + ".post_id " + from + " " + customWhereStatement +
                                " ) " + orderBy + " " + pagination + ")";

        final Collection<Post> results = executeQuery(select, from, newWhere, orderBy, args);

        return new PaginatedCollection<>(results, pageNumber, pageSize, totalPostCount);
    }

    private String buildOrderByStatement(SortCriteria sortCriteria) {

        if(!sortCriteriaQueryMap.containsKey(sortCriteria))
            throw new IllegalArgumentException("SortCriteria implementation not found for " + sortCriteria + " in PostDaoImpl.");

        return "ORDER BY " + sortCriteriaQueryMap.get(sortCriteria);
    }

    private String buildLimitAndOffsetStatement(int pageNumber, int pageSize) {

        if (pageNumber < 0 || pageSize <= 0)
            throw new IllegalArgumentException("Illegal Posts pagination arguments. Page Number: " + pageNumber + ". Page Size: " + pageSize);

        return "LIMIT " + pageSize + " OFFSET " + (pageNumber * pageSize);
    }

    private PaginatedCollection<Post> searchPostsByIntersectingFilterCriteria(FilterCriteria[] filterCriteria, Object[] args, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        String customWhereStatement = buildWhereStatement(filterCriteria," AND ");

        return buildAndExecutePaginatedQuery(customWhereStatement, sortCriteria, pageNumber, pageSize, args);
    }

    private String buildWhereStatement(FilterCriteria[] filters, String criteria) {
        return buildQueryStatement("WHERE", criteria,
                Arrays.stream(filters).map(f -> f.filterQuery).toArray(String[]::new));
    }

    // Separator needs to come with white spaces included
    private String buildQueryStatement(String queryStart, String separator, String[] modifiers) {
        if(modifiers == null || modifiers.length == 0)
            return "";

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append(queryStart).append(' ');

        for(String modifier : modifiers){

            queryBuilder.append(modifier);
            queryBuilder.append(separator);
        }

        //Delete last separator
        return queryBuilder.substring(0, queryBuilder.length() - separator.length());
    }

    @Override
    public Optional<Post> findPostById(long id){
        return buildAndExecuteQuery("WHERE " + POSTS + ".post_id = ? AND " + FilterCriteria.ENABLED.filterQuery, new Object[]{ id })
                .stream().findFirst();
    }

    @Override
    public PaginatedCollection<Post> findPostsByMovieId(long movie_id, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery("WHERE " +
                        POSTS + ".post_id IN ( " +
                        "SELECT " + POST_MOVIE + ".post_id " +
                        "FROM " + POST_MOVIE +
                        " WHERE " + POST_MOVIE + ".movie_id = ?) AND " + FilterCriteria.ENABLED.filterQuery,
                sortCriteria, pageNumber, pageSize, new Object[] { movie_id });
    }

    @Override
    public PaginatedCollection<Post> findPostsByUserId(long user_id, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery("WHERE " + POSTS + ".user_id = ? AND " + FilterCriteria.ENABLED.filterQuery,
                sortCriteria, pageNumber, pageSize, new Object[]{ user_id });
    }

    @Override
    public PaginatedCollection<Post> getAllPosts(SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery("WHERE " + FilterCriteria.ENABLED.filterQuery, sortCriteria, pageNumber, pageSize, null);
    }

    @Override
    public PaginatedCollection<Post> getDeletedPosts(SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery("WHERE " + FilterCriteria.NOT_ENABLED.filterQuery, sortCriteria, pageNumber, pageSize, null);
    }

    @Override
    public PaginatedCollection<Post> searchPosts(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        FilterCriteria[] filterCriteria = new FilterCriteria[]{
                FilterCriteria.BY_POST_TITLE_MOVIE_TITLE_AND_TAGS,
                FilterCriteria.ENABLED
        };

        Object[] args = new Object[]{
                query, query, query,
        };

        return searchPostsByIntersectingFilterCriteria(filterCriteria, args, sortCriteria, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Post> searchDeletedPosts(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        FilterCriteria[] filterCriteria = new FilterCriteria[]{
                FilterCriteria.BY_POST_TITLE_MOVIE_TITLE_AND_TAGS,
                FilterCriteria.NOT_ENABLED
        };

        Object[] args = new Object[]{
                query, query, query,
        };

        return searchPostsByIntersectingFilterCriteria(filterCriteria, args, sortCriteria, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Post> searchPostsByCategory(String query, String category, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        FilterCriteria[] filterCriteria = new FilterCriteria[]{
                FilterCriteria.BY_POST_TITLE_MOVIE_TITLE_AND_TAGS,
                FilterCriteria.BY_POST_CATEGORY,
                FilterCriteria.ENABLED
        };

        Object[] args = new Object[]{
                query, query, query,
                category
        };

        return searchPostsByIntersectingFilterCriteria(filterCriteria, args, sortCriteria, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Post> searchPostsOlderThan(String query, LocalDateTime fromDate, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        FilterCriteria[] filterCriteria = new FilterCriteria[]{
                FilterCriteria.BY_POST_TITLE_MOVIE_TITLE_AND_TAGS,
                FilterCriteria.POSTS_OLDER_THAN,
                FilterCriteria.ENABLED
        };

        Object[] args = new Object[]{
                query, query, query,
                Timestamp.valueOf(fromDate)
        };

        return searchPostsByIntersectingFilterCriteria(filterCriteria, args, sortCriteria, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Post> searchPostsByCategoryAndOlderThan(String query, String category, LocalDateTime fromDate, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        FilterCriteria[] filterCriteria = new FilterCriteria[]{
                FilterCriteria.BY_POST_TITLE_MOVIE_TITLE_AND_TAGS,
                FilterCriteria.BY_POST_CATEGORY,
                FilterCriteria.POSTS_OLDER_THAN,
                FilterCriteria.ENABLED
        };

        Object[] args = new Object[]{
                query, query, query,
                category,
                Timestamp.valueOf(fromDate)
        };

        return searchPostsByIntersectingFilterCriteria(filterCriteria, args, sortCriteria, pageNumber, pageSize);
    }
}