package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
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
public class CommentDaoImpl implements CommentDao {

    // Constants with Table Names
    private static final String COMMENTS = TableNames.COMMENTS.getTableName();
    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String POST_CATEGORY = TableNames.POST_CATEGORY.getTableName();
    private static final String USERS = TableNames.USERS.getTableName();
    private static final String ROLES = TableNames.ROLES.getTableName();
    private static final String USER_ROLE = TableNames.USER_ROLE.getTableName();
    private static final String COMMENTS_LIKES = TableNames.COMMENTS_LIKES.getTableName();

    private static final int MAX_CHILDREN_PAGINATION_DEPTH = 5;

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert commentInsert;

    private static final String BASE_COMMENT_SELECT = "SELECT " +
            COMMENTS + ".comment_id c_comment_id, " +
            "coalesce(" + COMMENTS + ".parent_id, 0) c_parent_id, " +
            COMMENTS + ".post_id c_post_id, " +
            COMMENTS + ".creation_date c_creation_date, " +
            COMMENTS + ".body c_body, " +
            COMMENTS + ".enabled c_enabled";


    private static final String LIKES_SELECT = COMMENTS_LIKES + ".likes c_likes";

    private static final String VOTED_BY_SELECT = "voted_by.user_id cl_user_id, " +
                "voted_by.value cl_value";

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
            POSTS + ".u_description pu_description, " +
            POSTS + ".u_avatar_id pu_avatar_id, " +
            POSTS + ".u_enabled pu_enabled";

    // Users come without roles
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

    private static final String BASE_COMMENT_FROM = "FROM " + COMMENTS;

    private static final String LIKES_FROM =  " INNER JOIN ( " +
            "SELECT " +
            COMMENTS + ".comment_id, " +
            "COALESCE(SUM( " + COMMENTS_LIKES + ".value ), 0) likes " +
            "FROM " + COMMENTS + " LEFT OUTER JOIN " +  COMMENTS_LIKES + " ON " + COMMENTS + ".comment_id = " + COMMENTS_LIKES + ".comment_id" +
            " GROUP BY " + COMMENTS + ".comment_id" +
            ") " + COMMENTS_LIKES  + " ON " + COMMENTS + ".comment_id = " + COMMENTS_LIKES + ".comment_id";

    private static final String VOTED_BY_FROM =  " LEFT OUTER JOIN ( " +
            "SELECT " +
            COMMENTS_LIKES + ".comment_id, " +
            COMMENTS_LIKES + ".user_id, " +
            COMMENTS_LIKES + ".value " +
            "FROM " + COMMENTS_LIKES +
            ") voted_by"  + " ON " + COMMENTS + ".comment_id = voted_by.comment_id";

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
                    USERS + ".description u_description, " +
                    USERS + ".avatar_id u_avatar_id, " +
                    USERS + ".enabled u_enabled" +

                    " FROM " + POSTS +
                        " INNER JOIN " + POST_CATEGORY + " ON " + POSTS + ".category_id = " + POST_CATEGORY + ".category_id " +
                        " INNER JOIN " + USERS + " ON " + POSTS + ".user_id = " + USERS + ".user_id " +
                    ") " + POSTS + " ON " + POSTS + ".post_id = " + COMMENTS + ".post_id";

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

            ") " + USERS + " ON " + USERS + ".user_id = " + COMMENTS + ".user_id";

    private static final ResultSetExtractor<Collection<Comment>> COMMENT_ROW_MAPPER = (rs) -> {

        final Map<Long, Comment> idToCommentMap = new LinkedHashMap<>();
        final Map<Long, Role> idToRoleMap = new HashMap<>();

        long comment_id;
        long role_id;
        long user_id;

        while(rs.next()) {

            comment_id = rs.getLong("c_comment_id");

            if(comment_id != 0 && !idToCommentMap.containsKey(comment_id)) {

                idToCommentMap.put(comment_id, new Comment(

                        rs.getLong("c_comment_id"), rs.getObject("c_creation_date", LocalDateTime.class),

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
                                        rs.getString("u_description"), rs.getLong("pu_avatar_id"), 0,
                                        null, rs.getBoolean("pu_enabled")),

                                // tags
                                null, rs.getBoolean("p_enabled"), 0),

                        rs.getLong("c_parent_id"), null, rs.getString("c_body"),

                        new User(rs.getLong("u_user_id"), rs.getObject("u_creation_date", LocalDateTime.class),
                                rs.getString("u_username"), rs.getString("u_password"),
                                rs.getString("u_name"), rs.getString("u_email"),
                                rs.getString("u_description"), rs.getLong("u_avatar_id"), 0,
                                new HashSet<>(), rs.getBoolean("u_enabled")),

                        rs.getBoolean("c_enabled"), rs.getLong("c_likes"), new HashMap<>()
                ));
            }

            role_id = rs.getLong("u_role_id");

            if(role_id > 0 && !idToRoleMap.containsKey(role_id))
                idToRoleMap.put(role_id, new Role(role_id, rs.getString("u_role")));

            idToCommentMap.get(comment_id).getUser().getRoles().add(idToRoleMap.get(role_id));

            user_id = rs.getLong("cl_user_id");

            if(user_id > 0 && !idToCommentMap.get(comment_id).getVotedBy().containsKey(user_id))
                idToCommentMap.get(comment_id).getVotedBy().put(user_id, rs.getLong("cl_value") > 0);

        }

        return idToCommentMap.values();
    };

    // Coalesce parent_id = null to parent_id = 0.
    private static final ResultSetExtractor<Collection<Comment>> COMMENT_ROW_MAPPER_WITH_CHILDREN = (rs) -> {
        final List<Comment> result = new ArrayList<>();
        final Map<Long, Comment> idToCommentMap = new HashMap<>();
        final Map<Long, Collection<Comment>> childrenWithoutParentMap = new HashMap<>();
        final Map<Long, Role> idToRoleMap = new HashMap<>();

        long comment_id;
        long role_id;
        long user_id;
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
                                        rs.getString("pu_description"), rs.getLong("pu_avatar_id"), 0,
                                        null, rs.getBoolean("pu_enabled")),

                                // tags
                                null, rs.getBoolean("p_enabled"), 0),

                        rs.getLong("c_parent_id"), new ArrayList<>(), rs.getString("c_body"),

                        new User(rs.getLong("u_user_id"), rs.getObject("u_creation_date", LocalDateTime.class),
                                rs.getString("u_username"), rs.getString("u_password"),
                                rs.getString("u_name"), rs.getString("u_email"),
                                rs.getString("u_description"), rs.getLong("u_avatar_id"), 0,
                                new HashSet<>(), rs.getBoolean("u_enabled")),

                        rs.getBoolean("c_enabled"), rs.getLong("c_likes"), new HashMap<>()
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

            role_id = rs.getLong("u_role_id");

            if(role_id > 0 && !idToRoleMap.containsKey(role_id))
                idToRoleMap.put(role_id, new Role(role_id, rs.getString("u_role")));

            idToCommentMap.get(comment_id).getUser().getRoles().add(idToRoleMap.get(role_id));

            user_id = rs.getLong("cl_user_id");

            if(user_id > 0 && !idToCommentMap.get(comment_id).getVotedBy().containsKey(user_id))
                idToCommentMap.get(comment_id).getVotedBy().put(user_id, rs.getLong("cl_value") > 0);

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
        sortCriteriaQuery.put(SortCriteria.HOTTEST, COMMENTS_LIKES + ".likes desc");

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
    public Comment register(Post post, Comment parent, String body, User user, boolean enabled) {

        Objects.requireNonNull(body);

        final Long parentId = (parent == null)? null : parent.getId();

        LocalDateTime creationDate = LocalDateTime.now();

        HashMap<String, Object> map = new HashMap<>();

        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("post_id", post.getId());
        map.put("parent_id", parentId);
        map.put("body", body);
        map.put("user_id", user.getId());
        map.put("enabled", enabled);

        final long commentId = commentInsert.executeAndReturnKey(map).longValue();

        return new Comment(commentId, creationDate, post, parentId, Collections.emptyList(),
                body, user, enabled, 0, Collections.emptyMap());
    }

    @Override
    public void likeComment(Comment comment, User user, int value) {

        jdbcTemplate.update(
                "INSERT INTO " + COMMENTS_LIKES + " (comment_id, user_id, value) VALUES ( ?, ?, ?) " +
                        "ON CONFLICT (comment_id, user_id) DO UPDATE SET value = ? ", comment.getId(), user.getId(), value, value);
    }

    @Override
    public void removeLike(Comment comment, User user) {
        jdbcTemplate.update( "DELETE FROM " + COMMENTS_LIKES + " WHERE " + COMMENTS_LIKES + ".comment_id = ? " + " AND "+ COMMENTS_LIKES + ".user_id = ?",
                comment.getId(), user.getId());
    }

    @Override
    public void deleteComment(Comment comment) {
        jdbcTemplate.update("UPDATE " + COMMENTS + " SET enabled = false WHERE comment_id = ?", comment.getId());
    }

    @Override
    public void restoreComment(Comment comment) {
        jdbcTemplate.update("UPDATE " + COMMENTS + " SET enabled = true WHERE comment_id = ?", comment.getId());
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

        final String select = buildSelectStatement();

        final String from = buildFromStatement();

        return executeQuery(select, from, customWhereStatement, "", args, false);
    }

    // The only way to paginate comments with children is to only ask for 1 Comment/Post (only 1 root in comment tree)
    // Then, the pagination is done over the first level of the tree.
    // Also, the comment tree returned will have a maximum height of MAX_CHILDREN_PAGINATION_DEPTH.
    // It is important to consider we couldn't limit the width of the tree, only it's height.
    private PaginatedCollection<Comment> getPaginatedChildrenQuery(SortCriteria sortCriteria, int pageNumber, int pageSize, Long rootId, boolean isRootPost) {

        final String select = buildSelectStatement();

        final String nonBaseFrom = buildNonBaseFromStatement();

        final String from = buildFromStatement();

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

        final String newWhere = "WHERE " + COMMENTS + ".comment_id IN ( " +
                "SELECT AUX.comment_id " +
                "FROM (" +
                "SELECT ROW_NUMBER() OVER(" + orderBy + ") row_num, " + COMMENTS + ".comment_id " +
                from + " " +
                firstLevelCommentsWhere +
                " ) AUX " +
                "GROUP BY AUX.comment_id " +
                "ORDER BY MIN(AUX.row_num) " +
                pagination + ")";

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

        final String select = buildSelectStatement();

        final String from = buildFromStatement();

        // Execute original query to count total comments in the query
        final int totalCommentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT " + COMMENTS + ".comment_id) " + from + " " + customWhereStatement, args, Integer.class);


        final String pagination = buildLimitAndOffsetStatement(pageNumber, pageSize);

        final String orderBy = buildOrderByStatement(sortCriteria);

        final String newWhere = "WHERE " + COMMENTS + ".comment_id IN ( " +
                "SELECT AUX.comment_id " +
                "FROM (" +
                "SELECT ROW_NUMBER() OVER(" + orderBy + ") row_num, " + COMMENTS + ".comment_id " +
                from + " " +
                customWhereStatement +
                " ) AUX " +
                "GROUP BY AUX.comment_id " +
                "ORDER BY MIN(AUX.row_num) " +
                pagination + ")";


        final Collection<Comment> results = executeQuery(select, from, newWhere, orderBy, args, false);

        return new PaginatedCollection<>(results, pageNumber, pageSize, totalCommentCount);
    }

    private String buildSelectStatement() {
        return BASE_COMMENT_SELECT + ", " + LIKES_SELECT + ", " + VOTED_BY_SELECT + ", " + POST_SELECT + ", " + USER_SELECT;
    }

    private String buildFromStatement() {
        return BASE_COMMENT_FROM + buildNonBaseFromStatement();
    }

    private String buildNonBaseFromStatement() {
        return LIKES_FROM + " " + VOTED_BY_FROM + " " + POST_FROM + " " + USER_FROM;
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
                "WHERE " + COMMENTS + ".comment_id = ?", new Object[]{ id })
                .stream().findFirst();
    }

    @Override
    public PaginatedCollection<Comment> findCommentDescendants(Comment comment, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return getPaginatedChildrenQuery(
                sortCriteria, pageNumber, pageSize, comment.getId(), false);
    }

    @Override
    public PaginatedCollection<Comment> findCommentChildren(Comment comment, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery("WHERE " + COMMENTS + ".parent_id = ?",
                sortCriteria, pageNumber, pageSize, new Object[]{ comment.getId() });
    }

    @Override
    public PaginatedCollection<Comment> findPostCommentDescendants(Post post, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return getPaginatedChildrenQuery(
                sortCriteria, pageNumber, pageSize, post.getId(), true);
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByPost(Post post, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery(
                "WHERE " + COMMENTS + ".post_id = ?", sortCriteria,
                pageNumber, pageSize, new Object[]{ post.getId() });
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByUser(User user, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery(
                "WHERE " + COMMENTS + ".user_id = ? AND " + COMMENTS + ".enabled = true", sortCriteria, pageNumber, pageSize,
                new Object[]{ user.getId() });
    }

    @Override
    public PaginatedCollection<Comment> getDeletedComments(SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery(
                "WHERE " + COMMENTS + ".enabled = false", sortCriteria, pageNumber, pageSize, null);
    }

    @Override
    public PaginatedCollection<Comment> searchDeletedComments(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery("WHERE " + COMMENTS + ".body ILIKE '%' || ? || '%' AND " + COMMENTS + ".enabled = false",
                sortCriteria, pageNumber, pageSize, new Object[]{ query });
    }
}
