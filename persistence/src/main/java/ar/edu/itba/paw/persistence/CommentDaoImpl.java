package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.InvalidPaginationArgumentException;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CommentDaoImpl implements CommentDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentDaoImpl.class);

    private static final String COMMENTS = Comment.TABLE_NAME;
    private static final String COMMENTS_LIKES = CommentLike.TABLE_NAME;

    private static final String NATIVE_BASE_COMMENT_FROM = "FROM " + COMMENTS;

    private static final String NATIVE_TOTAL_LIKES_FROM =  " INNER JOIN ( " +
            "SELECT " + COMMENTS + ".comment_id tl_comment_id, " + "COALESCE(SUM( " + COMMENTS_LIKES + ".value ), 0) total_likes " +
            "FROM " + COMMENTS +
                " LEFT OUTER JOIN " +  COMMENTS_LIKES + " ON " + COMMENTS + ".comment_id = " + COMMENTS_LIKES + ".comment_id" +
            " GROUP BY " + COMMENTS + ".comment_id" +
            ") " + COMMENTS_LIKES  + " ON " + COMMENTS + ".comment_id = " + COMMENTS_LIKES + ".tl_comment_id";

        private static final String NATIVE_PAGINATION_RECURSIVE_QUERY_UPPER =
            "WITH RECURSIVE comments_rec(comment_id, parent_id, post_id, user_id, creation_date, body, enabled, iteration) AS (" +
                "SELECT " +
                    "root_comments.comment_id, " +
                    "root_comments.parent_id, " +
                    "root_comments.post_id, " +
                    "root_comments.user_id, " +
                    "root_comments.creation_date, " +
                    "root_comments.body, " +
                    "root_comments.enabled, " +
                    "1 iteration";

        // The missing int value corresponds to the depth of the recursive query
    private static final String NATIVE_PAGINATION_RECURSIVE_QUERY_LOWER =
                " UNION " +
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
                "WHERE " + COMMENTS + ".parent_id = comments_rec.comment_id AND iteration < %d" +
            ")";


    private static final EnumMap<SortCriteria,String> sortCriteriaQueryMap = initializeSortCriteriaQueryMap();
    private static final EnumMap<SortCriteria,String> sortCriteriaHQLMap = initializeSortCriteriaHQL();

    private static EnumMap<CommentDao.SortCriteria, String> initializeSortCriteriaQueryMap() {
        final EnumMap<CommentDao.SortCriteria, String> sortCriteriaQuery = new EnumMap<>(CommentDao.SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, COMMENTS + ".creation_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, COMMENTS + ".creation_date");
        sortCriteriaQuery.put(SortCriteria.HOTTEST, COMMENTS_LIKES + ".total_likes desc");

        return sortCriteriaQuery;
    }

    private static EnumMap<CommentDao.SortCriteria, String> initializeSortCriteriaHQL() {
        final EnumMap<CommentDao.SortCriteria, String> sortCriteriaHQL = new EnumMap<>(CommentDao.SortCriteria.class);

        sortCriteriaHQL.put(SortCriteria.NEWEST, "c.creationDate desc");
        sortCriteriaHQL.put(SortCriteria.OLDEST, "c.creationDate");
        sortCriteriaHQL.put(SortCriteria.HOTTEST, "totalLikes desc");

        return sortCriteriaHQL;
    }

    @PersistenceContext
    private EntityManager em;

    @Override
    public Comment register(Post post, Comment parent, String body, User user, boolean enabled) {

        final Comment comment = new Comment(LocalDateTime.now(), post, parent, Collections.emptySet(), body, false, null, user, enabled, Collections.emptySet());

        em.persist(comment);

        comment.setTotalLikes(0L);

        LOGGER.info("Created Comment: {}", comment.getId());

        return comment;
    }

    @Override
    public int getVoteValue(Comment comment, User user) {
        CommentLike commentLike = em.createQuery("SELECT c FROM CommentLike c WHERE c.comment = :comment and c.user = :user", CommentLike.class)
                .setParameter("comment", comment)
                .setParameter("user", user)
                .getResultList().stream().findFirst().orElse(null);

        if (commentLike == null) {
            return 0;
        }

        return commentLike.getValue();
    }

    @Override
    public Optional<Comment> findCommentById(long id) {

        LOGGER.info("Find Comment By Id: {}", id);
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public Optional<Comment> findDeletedCommentById(long id) {

        LOGGER.info("Find Deleted Comment By Id: {}", id);

        TypedQuery<Comment> query = em.createQuery("SELECT c FROM Comment c WHERE c.id = :commentId AND enabled = :enabled", Comment.class)
                .setParameter("commentId", id)
                .setParameter("enabled", false);

        return query.getResultList().stream().findFirst();
    }

    @Override
    public PaginatedCollection<Comment> findCommentChildren(Comment comment, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Comment {} First Level Children Order By {}. Page number {}, Page Size {}", comment.getId(), sortCriteria, pageNumber, pageSize);

        return queryComments(
                "WHERE coalesce(" + COMMENTS + ".parent_id, 0) = ?" ,
                sortCriteria, pageNumber, pageSize, new Object[]{ comment.getId() });
    }

    @Override
    public PaginatedCollection<Comment> findCommentDescendants(Comment comment, long maxDepth, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Comment {} Descendants, Order By {}, Max Depth {}, Page number {}, Page Size {}", comment.getId(), sortCriteria, maxDepth, pageNumber, pageSize);

        return queryDescendantComments(sortCriteria, pageNumber, pageSize, comment.getId(), false, maxDepth);
    }

    @Override
    public PaginatedCollection<Comment> findPostCommentDescendants(Post post, long maxDepth, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Post {} Descendants, Order By {}, Max Depth {}, Page number {}, Page Size {}", post.getId(), sortCriteria, maxDepth, pageNumber, pageSize);

        return queryDescendantComments(sortCriteria, pageNumber, pageSize, post.getId(), true, maxDepth);
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByPost(Post post, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Comments By Post {} Order By {}. Page number {}, Page Size {}", post.getId(), sortCriteria, pageNumber, pageSize);

        return queryComments(
                "WHERE " + COMMENTS + ".post_id = ?", sortCriteria,
                pageNumber, pageSize, new Object[]{ post.getId() });
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByUser(User user, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Comments By User {} Order By {}. Page number {}, Page Size {}", user.getId(), sortCriteria, pageNumber, pageSize);

        return queryComments(
                "WHERE " + COMMENTS + ".user_id = ? AND " + COMMENTS + ".enabled = true",
                sortCriteria, pageNumber, pageSize, new Object[]{ user.getId() });
    }

    @Override
    public PaginatedCollection<Comment> getDeletedComments(SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Get all deleted Comments Order By {}. Page number {}, Page Size {}", sortCriteria, pageNumber, pageSize);

        return queryComments(
                "WHERE " + COMMENTS + ".enabled = false",
                sortCriteria, pageNumber, pageSize, null);
    }

    @Override
    public PaginatedCollection<Comment> searchDeletedComments(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Deleted Comments by Body {} Order By {}. Page number {}, Page Size {}", query, sortCriteria, pageNumber, pageSize);

        return queryComments(
                "WHERE LOWER(" + COMMENTS + ".body) LIKE '%' || LOWER(?) || '%' AND " + COMMENTS + ".enabled = false",
                sortCriteria, pageNumber, pageSize, new Object[]{ query });
    }

    @Override
    public PaginatedCollection<CommentLike> getCommentVotes(Comment comment, String sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    private String buildNativeFromStatement() {
        return NATIVE_BASE_COMMENT_FROM + " " + NATIVE_TOTAL_LIKES_FROM;
    }

    private String buildNativeOrderByStatement(SortCriteria sortCriteria) {

        if(!sortCriteriaQueryMap.containsKey(sortCriteria)) {
            LOGGER.error("SortCriteria Native implementation not found for {} in CommentDaoImpl", sortCriteria);
            throw new IllegalArgumentException();
        }

        return "ORDER BY " + sortCriteriaQueryMap.get(sortCriteria);
    }

    private String buildHQLOrderByStatement(SortCriteria sortCriteria) {

        if(!sortCriteriaHQLMap.containsKey(sortCriteria)) {
            LOGGER.error("SortCriteria HQL implementation not found for {} in CommentDaoImpl", sortCriteria);
            throw new IllegalArgumentException();
        }

        return "ORDER BY " + sortCriteriaHQLMap.get(sortCriteria);
    }

    private String buildNativePaginationStatement(int pageNumber, int pageSize) {

        if(pageNumber < 0 || pageSize <= 0) {
            LOGGER.error("Invalid pagination argument found in CommentDaoImpl. pageSize: {}, pageNumber: {}", pageSize, pageNumber);
            throw new InvalidPaginationArgumentException();
        }

        return String.format("LIMIT %d OFFSET %d", pageSize, pageNumber * pageSize);
    }

    private void addParamsToNativeQuery(Query query, Object[] params) {
        if(params == null)
            return;

        int i = 1;

        for(Object param : params) {
            query.setParameter(i, param);
            i++;
        }
    }

    private PaginatedCollection<Comment> queryComments(String nativeWhereStatement, SortCriteria sortCriteria, int pageNumber, int pageSize, Object[] params) {

        final String nativeSelect = "SELECT " + COMMENTS + ".comment_id";

        final String nativeCountSelect = "SELECT COUNT(DISTINCT " + COMMENTS + ".comment_id)";

        final String nativeFrom = buildNativeFromStatement();

        final String nativeOrderBy = buildNativeOrderByStatement(sortCriteria);

        final String HQLOrderBy = buildHQLOrderByStatement(sortCriteria);

        final String nativePagination = buildNativePaginationStatement(pageNumber, pageSize);

        final String nativeCountQuery = String.format("%s %s %s", nativeCountSelect, nativeFrom, nativeWhereStatement);

        final String nativeQuery = String.format("%s %s %s %s %s",
                nativeSelect, nativeFrom, nativeWhereStatement, nativeOrderBy, nativePagination);

        final String fetchQuery = String.format(
                "SELECT c, sum(coalesce(likes.value, 0)) AS totalLikes " +
                        "FROM Comment c LEFT OUTER JOIN c.likes likes " +
                        "WHERE c.id IN :commentIds " +
                        "GROUP BY c " +
                        "%s", HQLOrderBy);

        LOGGER.debug("QueryComments nativeCountQuery: {}", nativeCountQuery);
        LOGGER.debug("QueryComments nativeQuery: {}", nativeQuery);
        LOGGER.debug("QueryComments fetchQuery: {}", fetchQuery);

        // Calculate Total Comment Count Disregarding Pagination (To Calculate Pages Later)
        final Query totalCommentsNativeQuery = em.createNativeQuery(nativeCountQuery);

        addParamsToNativeQuery(totalCommentsNativeQuery, params);

        final long totalComments = ((Number) totalCommentsNativeQuery.getSingleResult()).longValue();

        if(totalComments == 0) {
            LOGGER.debug("QueryComments Total Count == 0");
            return new PaginatedCollection<>(Collections.emptyList(), pageNumber, pageSize, totalComments);
        }

        // Calculate Which Comments To Load And Load Their Ids
        final Query commentIdsNativeQuery = em.createNativeQuery(nativeQuery);

        addParamsToNativeQuery(commentIdsNativeQuery, params);

        @SuppressWarnings("unchecked")
        final Collection<Long> commentIds =
                ((List<Number>)commentIdsNativeQuery.getResultList())
                        .stream().map(Number::longValue).collect(Collectors.toList());

        if(commentIds.isEmpty()) {
            LOGGER.debug("QueryComments Empty Page");
            return new PaginatedCollection<>(Collections.emptyList(), pageNumber, pageSize, totalComments);
        }

        // Get Comments Based on Ids
        final Collection<Tuple> fetchQueryResult = em.createQuery(fetchQuery, Tuple.class)
                .setParameter("commentIds", commentIds)
                .getResultList();

        // Map Tuples To Comments
        final Collection<Comment> comments = fetchQueryResult.stream().map(tuple -> {

            tuple.get(0, Comment.class).setTotalLikes(tuple.get(1, Long.class));
            return tuple.get(0, Comment.class);

        }).collect(Collectors.toList());

        return new PaginatedCollection<>(comments, pageNumber, pageSize, totalComments);
    }

    private PaginatedCollection<Comment> queryDescendantComments(SortCriteria sortCriteria, int pageNumber, int pageSize, long rootId, boolean isRootPost, long maxDepth) {

        final String nativeCountSelect = "SELECT COUNT(DISTINCT " + COMMENTS + ".comment_id)";

        final String nativeFrom = buildNativeFromStatement();

        final String firstLevelCommentsWhere = isRootPost?

                // parent_id is null (is root) and it's post is rootId
                "WHERE " + COMMENTS + ".post_id = :rootId AND coalesce(" + COMMENTS + ".parent_id, 0) = 0" :

                // It's parent_id is a comment
                "WHERE coalesce(" + COMMENTS + ".parent_id, 0) = :rootId";

        final String nativeOrderBy = buildNativeOrderByStatement(sortCriteria);

        final String HQLOrderBy = buildHQLOrderByStatement(sortCriteria);

        final String nativePagination = buildNativePaginationStatement(pageNumber, pageSize);

        final String nativeCountQuery = String.format("%s %s %s", nativeCountSelect, nativeFrom, firstLevelCommentsWhere);

        // Select First Level And All Their Descendants
        final String recursiveNativeQuery = String.format(
                        NATIVE_PAGINATION_RECURSIVE_QUERY_UPPER +
                        " FROM (SELECT * %s %s %s %s ) root_comments " +
                        NATIVE_PAGINATION_RECURSIVE_QUERY_LOWER +
                        "SELECT comments_rec.comment_id FROM comments_rec",
                nativeFrom, firstLevelCommentsWhere, nativeOrderBy, nativePagination, maxDepth);

        final String fetchQuery = String.format(
                "SELECT c, sum(coalesce(likes.value, 0)) AS totalLikes " +
                        "FROM Comment c LEFT OUTER JOIN c.likes likes " +
                        "WHERE c.id IN :commentIds " +
                        "GROUP BY c " +
                        "%s", HQLOrderBy);

        LOGGER.debug("QueryDescendantComments nativeCountQuery: {}", nativeCountQuery);
        LOGGER.debug("QueryDescendantComments recursiveNativeQuery: {}", recursiveNativeQuery);
        LOGGER.debug("QueryDescendantComments fetchQuery: {}", fetchQuery);

        // Calculate First Level Comment Count Disregarding Pagination (To Calculate Pages Later)
        final Query totalCommentsNativeQuery =
                em.createNativeQuery(nativeCountQuery).setParameter("rootId", rootId);

        final long totalFirstLevelComments = ((Number) totalCommentsNativeQuery.getSingleResult()).longValue();

        if(totalFirstLevelComments == 0) {
            LOGGER.debug("QueryCommentsDescendants Total Count == 0");
            return new PaginatedCollection<>(Collections.emptyList(), pageNumber, pageSize, totalFirstLevelComments);
        }

        // Calculate Which Comments To Load And Load Their Ids
        final Query commentIdsNativeQuery =
                em.createNativeQuery(recursiveNativeQuery).setParameter("rootId", rootId);

        @SuppressWarnings("unchecked")
        final Collection<Long> commentIds =
                ((List<Number>)commentIdsNativeQuery.getResultList())
                        .stream().map(Number::longValue).collect(Collectors.toList());

        if(commentIds.isEmpty()) {
            LOGGER.debug("QueryCommentsDescendants Empty Page");
            return new PaginatedCollection<>(Collections.emptyList(), pageNumber, pageSize, totalFirstLevelComments);
        }

        // Get Comments Based on Ids
        final Collection<Tuple> fetchQueryResult = em.createQuery(fetchQuery, Tuple.class)
                .setParameter("commentIds", commentIds)
                .getResultList();

        // Map Tuples To Comments
        final Collection<Comment> allComments = fetchQueryResult.stream().map(tuple -> {

            tuple.get(0, Comment.class).setTotalLikes(tuple.get(1, Long.class));
            return tuple.get(0, Comment.class);

        }).collect(Collectors.toList());

        // I Just Return First Level, But The Other Comments Are Still Loaded In The Session (Referenced In The Tree Structure)
        final Collection<Comment> firstLevelComment = allComments.stream()
                .filter(c -> (isRootPost && c.getParent() == null) || (!isRootPost && c.getParent().getId() == rootId))
                .collect(Collectors.toList());

        return new PaginatedCollection<>(firstLevelComment, pageNumber, pageSize, totalFirstLevelComments);
    }
}
