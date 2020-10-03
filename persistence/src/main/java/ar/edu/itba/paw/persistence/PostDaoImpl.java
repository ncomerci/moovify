package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategory;
import ar.edu.itba.paw.models.User;
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
    private static final String TAGS = TableNames.TAGS.getTableName();
    private static final String POST_CATEGORY = TableNames.POST_CATEGORY.getTableName();
    private static final String USERS = TableNames.USERS.getTableName();


    private static final String BASE_POST_SELECT = "SELECT " +
            // Posts Table Columns - Alias: p_column_name
            POSTS + ".post_id p_post_id, " +
            POSTS + ".creation_date p_creation_date, " +
            POSTS + ".title p_title, " +
            POSTS + ".body p_body, " +
            POSTS + ".word_count p_word_count";

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
            USERS + ".enabled u_enabled";

    private static final String TAGS_SELECT = TAGS + ".tag p_tag";

    private static final String BASE_POST_FROM = "FROM " + POSTS;

    private static final String CATEGORY_FROM =
            "INNER JOIN " + POST_CATEGORY + " ON " + POSTS + ".category_id = " + POST_CATEGORY + ".category_id";

    private static final String USER_FROM =
            "INNER JOIN " + USERS + " ON " + USERS + ".user_id = " + POSTS + ".user_id";

    private static final String TAGS_FROM =
            "LEFT OUTER JOIN " + TAGS + " ON " + POSTS + ".post_id = " + TAGS + ".post_id";


    private static final ResultSetExtractor<Collection<Post>> POST_ROW_MAPPER = (rs) -> {

        final Map<Long, Post> idToPostMap = new LinkedHashMap<>();
        long post_id;
        String tag;

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
                                        rs.getString("u_name"), rs.getString("u_email"),
                                        null, rs.getBoolean("u_enabled")),

                                // tags
                                new LinkedHashSet<>()
                        )
                );
            }

            tag = rs.getString("p_tag");

            if (tag != null)
                idToPostMap.get(post_id).getTags().add(tag);
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
        );

        final String filterQuery;

        FilterCriteria(String filterQuery) {
            this.filterQuery = filterQuery;
        }
    }

    private static final EnumMap<SortCriteria,String> sortCriteriaQueryMap = initializeSortCriteriaQuery();

    private static EnumMap<SortCriteria, String> initializeSortCriteriaQuery() {
        EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, POSTS + ".creation_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, POSTS + ".creation_date");

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
    public long register(String title, String body, int wordCount, long categoryId, long userId, Set<String> tags, Set<Long> movies) {

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

    // This method abstract the logic needed to perform select queries with or without movies.
    private Collection<Post> buildAndExecuteQuery(String customWhereStatement, String customOrderByStatement, Object[] args){

        final String select = BASE_POST_SELECT + ", " + CATEGORY_SELECT + ", " + USER_SELECT + ", " + TAGS_SELECT;

        final String from = BASE_POST_FROM + " " + CATEGORY_FROM + " " + USER_FROM + " " + TAGS_FROM;

        final String query = select + " " + from + " " + customWhereStatement + " " + customOrderByStatement;

        if(args != null)
            return jdbcTemplate.query(query, args, POST_ROW_MAPPER);

        else
            return jdbcTemplate.query(query, POST_ROW_MAPPER);
    }
    
    private Collection<Post> buildAndExecuteQuery(String customWhereStatement, SortCriteria sortCriteria, Object[] args){
        
        if(!sortCriteriaQueryMap.containsKey(sortCriteria))
            throw new IllegalArgumentException("SortCriteria implementation not found for " + sortCriteria + " in PostDaoImpl.");
        
        return buildAndExecuteQuery(customWhereStatement,
                "ORDER BY " + sortCriteriaQueryMap.get(sortCriteria), args);
    }

    private Collection<Post> searchPostsByIntersectingFilterCriteria(FilterCriteria[] filterCriteria, Object[] args, SortCriteria sortCriteria){

        String customWhereStatement = buildWhereStatement(filterCriteria," AND ");

        return buildAndExecuteQuery(customWhereStatement, sortCriteria, args);
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
        return buildAndExecuteQuery("WHERE " + POSTS + ".post_id = ?", "",
                new Object[]{ id }).stream().findFirst();
    }

    @Override
    public Collection<Post> findPostsByMovieId(long movie_id, SortCriteria sortCriteria) {
        return buildAndExecuteQuery("WHERE " +
                        POSTS + ".post_id IN ( " +
                        "SELECT " + POST_MOVIE + ".post_id " +
                        "FROM " + POST_MOVIE +
                        " WHERE " + POST_MOVIE + ".movie_id = ?)",
                sortCriteria, new Object[] { movie_id });
    }

    @Override
    public Collection<Post> findPostsByUserId(long user_id, SortCriteria sortCriteria) {
        return buildAndExecuteQuery("WHERE " + POSTS + ".user_id = ?", sortCriteria,
                new Object[]{ user_id });
    }

    @Override
    public Collection<Post> getAllPosts(SortCriteria sortCriteria) {
        return buildAndExecuteQuery("", sortCriteria, null);
    }
    
    @Override
    public Collection<Post> searchPosts(String query, SortCriteria sortCriteria) {

        FilterCriteria[] filterCriteria = new FilterCriteria[]{
                FilterCriteria.BY_POST_TITLE_MOVIE_TITLE_AND_TAGS
        };

        Object[] args = new Object[]{
                query, query, query,
        };

        return searchPostsByIntersectingFilterCriteria(filterCriteria, args, sortCriteria);
    }

    @Override
    public Collection<Post> searchPostsByCategory(String query, String category, SortCriteria sortCriteria) {

        FilterCriteria[] filterCriteria = new FilterCriteria[]{
                FilterCriteria.BY_POST_TITLE_MOVIE_TITLE_AND_TAGS,
                FilterCriteria.BY_POST_CATEGORY
        };

        Object[] args = new Object[]{
                query, query, query,
                category
        };

        return searchPostsByIntersectingFilterCriteria(filterCriteria, args, sortCriteria);
    }

    @Override
    public Collection<Post> searchPostsOlderThan(String query, LocalDateTime fromDate, SortCriteria sortCriteria) {

        FilterCriteria[] filterCriteria = new FilterCriteria[]{
                FilterCriteria.BY_POST_TITLE_MOVIE_TITLE_AND_TAGS,
                FilterCriteria.POSTS_OLDER_THAN
        };

        Object[] args = new Object[]{
                query, query, query,
                Timestamp.valueOf(fromDate)
        };

        return searchPostsByIntersectingFilterCriteria(filterCriteria, args, sortCriteria);
    }

    @Override
    public Collection<Post> searchPostsByCategoryAndOlderThan(String query, String category, LocalDateTime fromDate, SortCriteria sortCriteria) {

        FilterCriteria[] filterCriteria = new FilterCriteria[]{
                FilterCriteria.BY_POST_TITLE_MOVIE_TITLE_AND_TAGS,
                FilterCriteria.BY_POST_CATEGORY,
                FilterCriteria.POSTS_OLDER_THAN
        };

        Object[] args = new Object[]{
                query, query, query,
                category,
                Timestamp.valueOf(fromDate)
        };

        return searchPostsByIntersectingFilterCriteria(filterCriteria, args, sortCriteria);
    }
}