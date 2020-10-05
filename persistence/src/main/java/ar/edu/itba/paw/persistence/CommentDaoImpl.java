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
    private static final String COMMENTS_LIKES = TableNames.COMMENTS_LIKES.getTableName();

    private static final int MAX_CHILDREN_PAGINATION_DEPTH = 5;

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert commentInsert;
    private final SimpleJdbcInsert commentLikesInsert;

    private static final String BASE_COMMENT_SELECT = "SELECT " +
            COMMENTS + ".comment_id c_comment_id, " +
            "coalesce(" + COMMENTS + ".parent_id, 0) c_parent_id, " +
            COMMENTS + ".post_id c_post_id, " +
            COMMENTS + ".creation_date c_creation_date, " +
            COMMENTS + ".body c_body, " +
            COMMENTS + ".enabled c_enabled";


    private static final String LIKES_SELECT = COMMENTS_LIKES + ".likes c_likes";

    // Posts come without Tags
    private static final String POST_SELECT =
            POSTS + ".post_id p_post_id, " +
            POSTS + ".creation_date p_creation_date, " +
            POSTS + ".title p_title, " +
            POSTS + ".body p_body, " +
            POSTS + ".word_count p_word_count, " +
            POSTS + ".p_enabled p_enabled, " +

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
            POSTS + ".u_email pu_email, " +
            POSTS + ".u_enabled pu_enabled";

    // Users come without roles
    private static final String USER_SELECT =
            USERS + ".user_id u_user_id, " +
            USERS + ".creation_date u_creation_date, " +
            USERS + ".username u_username, " +
            USERS + ".password u_password, " +
            USERS + ".name u_name, " +
            USERS + ".email u_email, " +
            USERS + ".enabled u_enabled";

    private static final String BASE_COMMENT_FROM = "FROM " + COMMENTS;

    private static final String LIKES_FROM =  " LEFT OUTER JOIN ( " +
            "SELECT " +
            COMMENTS_LIKES + ".comment_id, " +
            "COUNT( " + COMMENTS_LIKES + ".user_id ) likes " +
            "FROM " + COMMENTS_LIKES +
            " GROUP BY " + COMMENTS_LIKES + ".comment_id" +
            ") " + COMMENTS_LIKES  + " ON " + COMMENTS + ".comment_id =" + COMMENTS_LIKES + ".comment_id";

    private static final String POST_FROM =
            "INNER JOIN ( " +
                    "SELECT " +

                    POSTS + ".post_id, " +
                    POSTS + ".creation_date, " +
                    POSTS + ".title, " +
                    POSTS + ".body, " +
                    POSTS + ".word_count, " +
                    POSTS + ".enabled p_enabled, " +

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
                    USERS + ".email u_email, " +
                    USERS + ".enabled u_enabled" +

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
                                    rs.getString("u_description"),
                                    null, rs.getBoolean("pu_enabled"), null),

                            // tags
                            null, rs.getBoolean("p_enabled"), 0),

                    rs.getLong("c_parent_id"), null, rs.getString("c_body"),

                    new User(rs.getLong("u_user_id"), rs.getObject("u_creation_date", LocalDateTime.class),
                            rs.getString("u_username"), rs.getString("u_password"),
                            rs.getString("u_name"), rs.getString("u_email"),
                            rs.getString("u_description"),
                            null, rs.getBoolean("u_enabled"), null),
                    rs.getBoolean("c_enabled"),
                    rs.getLong("c_likes")
                    );

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
                                        rs.getString("u_description"),
                                        null, rs.getBoolean("pu_enabled") , null),

                                // tags
                                null, rs.getBoolean("p_enabled"), 0),

                        rs.getLong("c_parent_id"), new ArrayList<>(), rs.getString("c_body"),

                        new User(rs.getLong("u_user_id"), rs.getObject("u_creation_date", LocalDateTime.class),
                                rs.getString("u_username"), rs.getString("u_password"),
                                rs.getString("u_name"), rs.getString("u_email"),
                                rs.getString("u_description"),
                                null, rs.getBoolean("u_enabled"), null), rs.getBoolean("c_enabled"), rs.getLong("c_likes")
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

        // Root comments must also be returned
        if(!childrenWithoutParentMap.isEmpty()) {
            for(Collection<Comment> rootComments : childrenWithoutParentMap.values())
                result.addAll(rootComments);
        }

        return result;
    };

    private static final String PAGINATION_RECURSIVE_QUERY_UPPER =
            "WITH RECURSIVE comments_rec AS (" +
                "SELECT " +
                    "root_comments.comment_id, " +
                    "root_comments.parent_id, " +
                    "root_comments.post_id, " +
                    "root_comments.user_id, " +
                    "root_comments.creation_date, " +
                    "root_comments.body, " +
                    "root_comments.enabled, " +
                    "1 iteration";

    private static final String PAGINATION_RECURSIVE_QUERY_LOWER =
                "UNION " +
                "SELECT " +
                    COMMENTS + ".comment_id, " +
                    COMMENTS + ".parent_id, " +
                    COMMENTS + ".post_id, " +
                    COMMENTS + ".user_id, " +
                    COMMENTS + ".creation_date, " +
                    COMMENTS + ".body, " +
                    COMMENTS + ".enabled, " +
                    "iteration+1 iteration " +
                "FROM " + COMMENTS + ", comments_rec " +
                "WHERE " + COMMENTS + ".parent_id = comments_rec.comment_id AND iteration < " + MAX_CHILDREN_PAGINATION_DEPTH +
            ")";

    private static final EnumMap<CommentDao.SortCriteria,String> sortCriteriaQueryMap = initializeSortCriteriaQuery();

    private static EnumMap<CommentDao.SortCriteria, String> initializeSortCriteriaQuery() {
        EnumMap<CommentDao.SortCriteria, String> sortCriteriaQuery = new EnumMap<>(CommentDao.SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, COMMENTS + ".creation_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, COMMENTS + ".creation_date");
        sortCriteriaQuery.put(SortCriteria.HOTTEST, COMMENTS_LIKES + ".likes");

        return sortCriteriaQuery;
    }

    @Autowired
    public CommentDaoImpl(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);

        commentInsert = new SimpleJdbcInsert(ds)
                .withTableName(COMMENTS)
                .usingGeneratedKeyColumns("comment_id");

        commentLikesInsert = new SimpleJdbcInsert(ds)
                .withTableName(COMMENTS_LIKES);
    }

    @Override
    public long register(long postId, Long parentId, String body, long userId, boolean enabled) {

        Objects.requireNonNull(body);

        LocalDateTime creationDate = LocalDateTime.now();

        HashMap<String, Object> map = new HashMap<>();
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("post_id", postId);
        map.put("parent_id", parentId);
        map.put("body", body);
        map.put("user_id", userId);
        map.put("enabled", enabled);

        return commentInsert.executeAndReturnKey(map).longValue();
    }

    @Override
    public void likeComment(long comment_id, long user_id) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("comment_id", comment_id);
        map.put("user_id", user_id);

        commentLikesInsert.execute(map);
    }

    @Override
    public void removeLike(long comment_id, long user_id) {
        jdbcTemplate.update(
                "DELETE FROM " + COMMENTS_LIKES + " WHERE " + COMMENTS_LIKES + ".comment_id = ? " + " AND "+ COMMENTS_LIKES + ".user_id = ?", comment_id, user_id );
    }


    private Collection<Comment> executeQuery(String select, String from, String where, String orderBy, Object[] args, boolean withChildren) {

        final String query = select + " " + from + " " + where + " " + orderBy;

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

    // You cannot ask comments with children without paginating
    private Collection<Comment> buildAndExecuteQuery(String customWhereStatement, Object[] args) {

        final String select = BASE_COMMENT_SELECT + ", " + LIKES_SELECT + ", " + POST_SELECT + ", " + USER_SELECT;

        final String from = BASE_COMMENT_FROM + " " + LIKES_FROM + " " + POST_FROM + " " + USER_FROM;

        return executeQuery(select, from, customWhereStatement, "", args, false);
    }

    // The only way to paginate comments with children is to only ask for 1 Comment/Post (only 1 root in comment tree)
    // Then, the pagination is done over the first level of the tree.
    // Also, the comment tree returned will have a maximum height of MAX_CHILDREN_PAGINATION_DEPTH.
    // It is important to consider we couldn't limit the width of the tree, only it's height.
    private PaginatedCollection<Comment> getPaginatedChildrenQuery(SortCriteria sortCriteria, int pageNumber, int pageSize, Long rootId, boolean isRootPost) {

        final String select = BASE_COMMENT_SELECT + ", " + LIKES_SELECT + ", " + POST_SELECT + ", " + USER_SELECT;

        final String nonBaseFrom = LIKES_FROM + " " + POST_FROM + " " + USER_FROM;

        final String from = BASE_COMMENT_FROM + " " + nonBaseFrom;

        final String firstLevelCommentsWhere = isRootPost?
                // parent_id is null (is root) and it's post is rootId
                "WHERE " + COMMENTS + ".post_id = ? AND coalesce(" + COMMENTS + ".parent_id, 0) = 0" :

                // It's parent_id is a comment
                "WHERE coalesce(" + COMMENTS + ".parent_id, 0) = ?";

        // Add rootId to args list
        final Object[] args = new Object[]{ rootId };


        // Execute original query to count total comments in the query
        final int totalCommentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT " + COMMENTS + ".comment_id) " + from + " " + firstLevelCommentsWhere, args, Integer.class);


        final String pagination = buildLimitAndOffsetStatement(pageNumber, pageSize);

        final String orderBy = buildOrderByStatement(sortCriteria);

        final String newWhere =
                "WHERE " + COMMENTS + ".comment_id IN (SELECT " + COMMENTS + ".comment_id FROM " + COMMENTS + " WHERE " + COMMENTS + ".comment_id IN (" +
                "SELECT " + COMMENTS + ".comment_id " + from + " " + firstLevelCommentsWhere +
                " ) " + orderBy + " " + pagination + ")";

        final String recursiveQuery =
                    PAGINATION_RECURSIVE_QUERY_UPPER +
                        " FROM (SELECT * FROM " + COMMENTS + " " + newWhere + ") root_comments " +
                        PAGINATION_RECURSIVE_QUERY_LOWER;

        final String recursiveSelect = recursiveQuery + " " + select;

        // Replaces BASE_COMMENT_FROM. It should not have logic!!
        final String recursiveFrom = "FROM (SELECT * FROM comments_rec) " + COMMENTS + " " + nonBaseFrom;

        final Collection<Comment> results = executeQuery(recursiveSelect, recursiveFrom, "", orderBy, args, true);

        return new PaginatedCollection<>(results, pageNumber, pageSize, totalCommentCount);
    }

    private PaginatedCollection<Comment> buildAndExecutePaginatedQuery(String customWhereStatement, SortCriteria sortCriteria, int pageNumber, int pageSize, Object[] args) {

        final String select = BASE_COMMENT_SELECT + ", " + LIKES_SELECT + ", " + POST_SELECT + ", " + USER_SELECT;

        final String from = BASE_COMMENT_FROM + " " + LIKES_FROM + " " + POST_FROM + " " + USER_FROM;

        // Execute original query to count total comments in the query
        final int totalCommentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT " + COMMENTS + ".comment_id) " + from + " " + customWhereStatement, args, Integer.class);


        final String pagination = buildLimitAndOffsetStatement(pageNumber, pageSize);

        final String orderBy = buildOrderByStatement(sortCriteria);

        final String newWhere =
                "WHERE " + COMMENTS + ".comment_id IN (SELECT " + COMMENTS + ".comment_id FROM " + COMMENTS + " WHERE " + COMMENTS + ".comment_id IN (" +
                        "SELECT " + COMMENTS + ".comment_id " + from + " " + customWhereStatement +
                        " ) " + orderBy + " " + pagination + ")";


        final Collection<Comment> results = executeQuery(select, from, newWhere, orderBy, args, false);

        return new PaginatedCollection<>(results, pageNumber, pageSize, totalCommentCount);
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

    @Override
    public Optional<Comment> findCommentById(long id) {
        return buildAndExecuteQuery(
                "WHERE " + COMMENTS + ".comment_id = ?", new Object[] { id })
                .stream().findFirst();
    }

    @Override
    public PaginatedCollection<Comment> findCommentDescendants(long commentId, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return getPaginatedChildrenQuery(
                sortCriteria, pageNumber, pageSize, commentId, false);
    }

    @Override
    public PaginatedCollection<Comment> findCommentChildren(long commentId, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery("WHERE " + COMMENTS + ".parent_id = ?",
                sortCriteria, pageNumber, pageSize, new Object[]{ commentId });
    }

    @Override
    public PaginatedCollection<Comment> findPostCommentDescendants(long post_id, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return getPaginatedChildrenQuery(
                sortCriteria, pageNumber, pageSize, post_id, true);
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByPostId(long post_id, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery(
                "WHERE " + COMMENTS + ".post_id = ?", sortCriteria,
                pageNumber, pageSize, new Object[] { post_id });
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByUserId(long user_id, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery(
                "WHERE " + COMMENTS + ".user_id = ?", sortCriteria, pageNumber, pageSize,
                new Object[] { user_id });
    }
}
