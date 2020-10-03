package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class CommentDaoImpl implements CommentDao {

    // Constants with Table Names
    private static final String COMMENTS = TableNames.COMMENTS.getTableName();
    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String POST_CATEGORY = TableNames.POST_CATEGORY.getTableName();
    private static final String USERS = TableNames.USERS.getTableName();

    private static final int MAX_PAGINATION_DEPTH = 8;

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert commentInsert;


    private static final String BASE_COMMENT_SELECT = "SELECT " +
            COMMENTS + ".comment_id c_comment_id, " +
            "coalesce(" + COMMENTS + ".parent_id, 0) c_parent_id, " +
            COMMENTS + ".post_id c_post_id, " +
            COMMENTS + ".creation_date c_creation_date, " +
            COMMENTS + ".body c_body";

    // Posts come without Tags
    private static final String POST_SELECT =
            POSTS + ".post_id p_post_id, " +
            POSTS + ".creation_date p_creation_date, " +
            POSTS + ".title p_title, " +
            POSTS + ".body p_body, " +
            POSTS + ".word_count p_word_count, " +

            // Post Category
            POSTS + ".c_category_id pc_category_id, " +
            POSTS + ".c_creation_date pc_creation_date, " +
            POSTS + ".c_name pc_name, " +

            // Post User
            POSTS + ".u_user_id pu_user_id, " +
            POSTS + ".u_creation_date pu_creation_date, " +
            POSTS + ".u_username pu_username, " +
            POSTS + ".u_password pu_password, " +
            POSTS + ".u_name pu_name, " +
            POSTS + ".u_email pu_email";

    // Users come without roles
    private static final String USER_SELECT =
            USERS + ".user_id u_user_id, " +
            USERS + ".creation_date u_creation_date, " +
            USERS + ".username u_username, " +
            USERS + ".password u_password, " +
            USERS + ".name u_name, " +
            USERS + ".email u_email";

    private static final String BASE_COMMENT_FROM = "FROM " + COMMENTS;

    private static final String POST_FROM =
            "INNER JOIN ( " +
                    "SELECT " +

                    POSTS + ".post_id, " +
                    POSTS + ".creation_date, " +
                    POSTS + ".title, " +
                    POSTS + ".body, " +
                    POSTS + ".word_count, " +

                    // Post Category
                    POST_CATEGORY + ".category_id c_category_id, " +
                    POST_CATEGORY + ".creation_date c_creation_date, " +
                    POST_CATEGORY + ".name c_name, " +

                    // Post User
                    USERS + ".user_id u_user_id, " +
                    USERS + ".creation_date u_creation_date, " +
                    USERS + ".username u_username, " +
                    USERS + ".password u_password, " +
                    USERS + ".name u_name, " +
                    USERS + ".email u_email" +

                    " FROM " + POSTS +
                        " INNER JOIN " + POST_CATEGORY + " ON " + POSTS + ".category_id = " + POST_CATEGORY + ".category_id " +
                        " INNER JOIN " + USERS + " ON " + POSTS + ".user_id = " + USERS + ".user_id " +
                    ") " + POSTS + " ON " + POSTS + ".post_id = " + COMMENTS + ".post_id";

    private static final String USER_FROM =
            "INNER JOIN " + USERS + " ON " + USERS + ".user_id = " + COMMENTS + ".user_id";

    private static final RowMapper<Comment> COMMENT_ROW_MAPPER = (rs, rowNum) ->
            new Comment(rs.getLong("c_comment_id"), rs.getObject("c_creation_date", LocalDateTime.class),

                    new Post(
                            rs.getLong("p_post_id"), rs.getObject("p_creation_date", LocalDateTime.class),
                            rs.getString("p_title"), rs.getString("p_body"),
                            rs.getInt("p_word_count"),

                            new PostCategory(rs.getLong("pc_category_id"),
                                    rs.getObject("pc_creation_date", LocalDateTime.class),
                                    rs.getString("pc_name")),

                            new User(rs.getLong("pu_user_id"), rs.getObject("pu_creation_date", LocalDateTime.class),
                                    rs.getString("pu_username"), rs.getString("pu_password"),
                                    rs.getString("pu_name"), rs.getString("pu_email"),
                                    null),

                            // tags
                            null),

                    rs.getLong("c_parent_id"), null, rs.getString("c_body"),

                    new User(rs.getLong("u_user_id"), rs.getObject("u_creation_date", LocalDateTime.class),
                            rs.getString("u_username"), rs.getString("u_password"),
                            rs.getString("u_name"), rs.getString("u_email"),
                            null));

    // Coalesce parent_id = null to parent_id = 0.
    private static final ResultSetExtractor<Collection<Comment>> COMMENT_ROW_MAPPER_WITH_CHILDREN = (rs) -> {
        List<Comment> result = new ArrayList<>();
        Map<Long, Comment> idToCommentMap = new HashMap<>();
        Map<Long, Collection<Comment>> childrenWithoutParentMap = new HashMap<>();

        long comment_id;
        Comment currentComment;

        while(rs.next()){

            comment_id = rs.getLong("c_comment_id");

            // Returns 0 on null
            if(comment_id != 0 && !idToCommentMap.containsKey(comment_id)) {

                currentComment = new Comment(comment_id,
                        rs.getObject("c_creation_date", LocalDateTime.class),

                        new Post(
                                rs.getLong("p_post_id"), rs.getObject("p_creation_date", LocalDateTime.class),
                                rs.getString("p_title"), rs.getString("p_body"),
                                rs.getInt("p_word_count"),

                                new PostCategory(rs.getLong("pc_category_id"),
                                        rs.getObject("pc_creation_date", LocalDateTime.class),
                                        rs.getString("pc_name")),

                                new User(rs.getLong("pu_user_id"), rs.getObject("pu_creation_date", LocalDateTime.class),
                                        rs.getString("pu_username"), rs.getString("pu_password"),
                                        rs.getString("pu_name"), rs.getString("pu_email"),
                                        null),

                                // tags
                                null),

                        rs.getLong("c_parent_id"), new ArrayList<>(), rs.getString("c_body"),

                        new User(rs.getLong("u_user_id"), rs.getObject("u_creation_date", LocalDateTime.class),
                                rs.getString("u_username"), rs.getString("u_password"),
                                rs.getString("u_name"), rs.getString("u_email"),
                                null)
                );

                idToCommentMap.put(comment_id, currentComment);

                // Incorporate all children that appeared before currentComment
                if(childrenWithoutParentMap.containsKey(comment_id)) {
                    currentComment.getChildren().addAll(childrenWithoutParentMap.get(comment_id));

                    // Mapping is no longer necessary
                    childrenWithoutParentMap.remove(comment_id);
                }

                // Comment is root
                if (currentComment.getParentId() == 0)
                    result.add(currentComment);

                else {
                    // If parent doesn't exist yet
                    if(!idToCommentMap.containsKey(currentComment.getParentId())) {

                        // Initialize Collection inside Map if necessary
                        if(!childrenWithoutParentMap.containsKey(currentComment.getParentId()))
                            childrenWithoutParentMap.put(currentComment.getParentId(), new ArrayList<>());

                        // Add children to Parent Children Buffer
                        childrenWithoutParentMap.get(currentComment.getParentId()).add(currentComment);
                    }

                    // Parent exists -> Add to parent
                    else
                        idToCommentMap.get(currentComment.getParentId()).getChildren().add(currentComment);
                }
            }
        }

        return result;
    };

    private static final String PAGINATION_RECURSIVE_QUERY_UPPER =
            "WITH RECURSIVE " + COMMENTS + " AS (" +
                "SELECT" +
                    "root_comments.comment_id," +
                    "root_comments.parent_id," +
                    "root_comments.post_id," +
                    "root_comments.creation_date," +
                    "root_comments.body," +
                    "1 iteration";

    private static final String PAGINATION_RECURSIVE_QUERY_LOWER =
                "UNION" +
                "SELECT" +
                    "root_comments.comment_id," +
                    "root_comments.parent_id," +
                    "root_comments.post_id," +
                    "root_comments.creation_date," +
                    "root_comments.body," +
                    "iteration+1 iteration" +
                "FROM root_comments, " + COMMENTS +
                "WHERE root_comments.comment_id = " + COMMENTS + ".parent_id AND iteration < " + MAX_PAGINATION_DEPTH +
            ")";

    private static final EnumMap<CommentDao.SortCriteria,String> sortCriteriaQueryMap = initializeSortCriteriaQuery();

    private static EnumMap<CommentDao.SortCriteria, String> initializeSortCriteriaQuery() {
        EnumMap<CommentDao.SortCriteria, String> sortCriteriaQuery = new EnumMap<>(CommentDao.SortCriteria.class);

        sortCriteriaQuery.put(CommentDao.SortCriteria.NEWEST, COMMENTS + ".creation_date desc");
        sortCriteriaQuery.put(CommentDao.SortCriteria.OLDEST, COMMENTS + ".creation_date");

        return sortCriteriaQuery;
    }

    @Autowired
    public CommentDaoImpl(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);

        commentInsert = new SimpleJdbcInsert(ds)
                .withTableName(COMMENTS)
                .usingGeneratedKeyColumns("comment_id");
    }

    @Override
    public long register(long postId, Long parentId, String body, long userId) {

        Objects.requireNonNull(body);

        LocalDateTime creationDate = LocalDateTime.now();

        HashMap<String, Object> map = new HashMap<>();
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("post_id", postId);
        map.put("parent_id", parentId);
        map.put("body", body);
        map.put("user_id", userId);

        return commentInsert.executeAndReturnKey(map).longValue();
    }

    private Collection<Comment> executeQuery(String select, String from, String where, Object[] args, boolean withChildren) {

        final String query = select + " " + from + " " + where;

        if(args != null){
            if(withChildren)
                return jdbcTemplate.query(query, args, COMMENT_ROW_MAPPER_WITH_CHILDREN);
            else
                return jdbcTemplate.query(query, args, COMMENT_ROW_MAPPER);
        }
        else {
            if(withChildren)
                return jdbcTemplate.query(query, COMMENT_ROW_MAPPER_WITH_CHILDREN);
            else
                return jdbcTemplate.query(query, COMMENT_ROW_MAPPER);
        }
    }

    private Collection<Comment> buildAndExecuteQuery(String customWhereStatement, Object[] args, boolean withChildren) {
        
        final String select = BASE_COMMENT_SELECT + ", " + POST_SELECT + ", " + USER_SELECT;

        final String from = BASE_COMMENT_FROM + " " + POST_FROM + " " + USER_FROM;

        return executeQuery(select, from, customWhereStatement, args, withChildren);
    }

    private PaginatedCollection<Comment> buildAndExecutePaginatedQuery(String customWhereStatement, SortCriteria sortCriteria, int pageNumber, int pageSize, Object[] args, boolean withChildren) {

        final String pagination = buildLimitAndOffsetStatement(pageNumber, pageSize);

        final String orderBy = buildOrderByStatement(sortCriteria);

//        final String recursiveQuery =
//                PAGINATION_RECURSIVE_QUERY_UPPER +
//                "FROM (SELECT * FROM " + COMMENTS + " WHERE coalesce(" + COMMENTS + ".parent_id, 0) = ?" + orderBy + " " + pagination + ") root_comments" +
//                PAGINATION_RECURSIVE_QUERY_LOWER;


        final String nonPaginatedFrom = BASE_COMMENT_FROM + " " + POST_FROM + " " + USER_FROM;

        // Execute original query to count total comments in the query
        final int totalCommentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT " + POSTS + ".post_id) " + nonPaginatedFrom + " " + customWhereStatement, args, Integer.class);


        final String select = BASE_COMMENT_SELECT + ", " + POST_SELECT + ", " + USER_SELECT;

        final String paginatedBaseCommentFrom = "FROM (SELECT * " + BASE_COMMENT_FROM + " " + orderBy + " " + pagination + ") " + COMMENTS;

        final String paginatedFrom = paginatedBaseCommentFrom + " " + POST_FROM + " " + USER_FROM;

        final Collection<Comment> results = executeQuery(select, paginatedFrom, customWhereStatement, args, withChildren);

        final boolean lastPage = totalCommentCount == 0 || (totalCommentCount - 1)/pageSize == pageNumber;

        return new PaginatedCollection<>(results, pageNumber, pageSize, totalCommentCount, lastPage);
    }

    private String buildOrderByStatement(SortCriteria sortCriteria) {

        if(!sortCriteriaQueryMap.containsKey(sortCriteria))
            throw new IllegalArgumentException("SortCriteria implementation not found for " + sortCriteria + " in CommentDaoImpl.");

        return "ORDER BY " + sortCriteriaQueryMap.get(sortCriteria);
    }

    private String buildLimitAndOffsetStatement(int pageNumber, int pageSize) {

        if(pageNumber < 0 || pageSize <= 0)
            throw new IllegalArgumentException("Illegal Comment pagination arguments. Page Number: " + pageNumber + ". Page Size: " + pageSize);

        return "LIMIT " + pageSize + " OFFSET " + (pageNumber * pageSize);
    }

    private Optional<Comment> findCommentById(long id, boolean withChildren) {
        return buildAndExecuteQuery(
                "WHERE " + COMMENTS + ".comment_id = ?", new Object[] { id }, withChildren)
                .stream().findFirst();
    }

    @Override
    public Optional<Comment> findCommentByIdWithChildren(long id) {
        return findCommentById(id, false);
    }

    @Override
    public Optional<Comment> findCommentByIdWithoutChildren(long id) {
        return findCommentById(id, true);
    }

    private PaginatedCollection<Comment> findCommentsByPostId(long post_id, SortCriteria sortCriteria, int pageNumber, int pageSize, boolean withChildren) {
        return buildAndExecutePaginatedQuery(
                "WHERE " + COMMENTS + ".post_id = ?", sortCriteria, pageNumber, pageSize,
                new Object[] { post_id }, withChildren);
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByPostIdWithChildren(long post_id, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return findCommentsByPostId(post_id, sortCriteria, pageNumber, pageSize, true);
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByPostIdWithoutChildren(long post_id, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return findCommentsByPostId(post_id, sortCriteria, pageNumber, pageSize, false);
    }

    private PaginatedCollection<Comment> findCommentsByUserId(long user_id, SortCriteria sortCriteria, int pageNumber, int pageSize, boolean withChildren) {
        return buildAndExecutePaginatedQuery(
                "WHERE " + COMMENTS + ".user_id = ?", sortCriteria, pageNumber, pageSize,
                new Object[] { user_id }, withChildren);
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByUserIdWithChildren(long user_id, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return findCommentsByUserId(user_id, sortCriteria, pageNumber, pageSize, true);
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByUserIdWithoutChildren(long user_id, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return findCommentsByUserId(user_id, sortCriteria, pageNumber, pageSize, false);
    }
}
