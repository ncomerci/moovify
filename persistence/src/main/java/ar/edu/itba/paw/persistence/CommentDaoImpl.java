package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.InvalidPaginationArgumentException;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CommentDaoImpl implements CommentDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentDaoImpl.class);

    private static final String COMMENTS = Comment.TABLE_NAME;
    private static final String COMMENTS_LIKES = CommentVote.TABLE_NAME;

    private static final String NATIVE_BASE_COMMENT_FROM = "FROM " + COMMENTS;

    private static final String NATIVE_TOTAL_LIKES_FROM =  " INNER JOIN ( " +
            "SELECT " + COMMENTS + ".comment_id tl_comment_id, " + "COALESCE(SUM( " + COMMENTS_LIKES + ".value ), 0) total_likes " +
            "FROM " + COMMENTS +
                " LEFT OUTER JOIN " +  COMMENTS_LIKES + " ON " + COMMENTS + ".comment_id = " + COMMENTS_LIKES + ".comment_id" +
            " GROUP BY " + COMMENTS + ".comment_id" +
            ") " + COMMENTS_LIKES  + " ON " + COMMENTS + ".comment_id = " + COMMENTS_LIKES + ".tl_comment_id";

    private static final String NATIVE_ENABLED_FILTER = COMMENTS + ".enabled = true";

    private static final String NATIVE_DISABLED_FILTER = COMMENTS + ".enabled = false";

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

        comment.setTotalVotes(0L);

        LOGGER.info("Created Comment: {}", comment.getId());

        return comment;
    }

    @Override
    public Optional<Comment> findCommentById(long id) {

        LOGGER.info("Find Comment By Id: {}", id);
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public PaginatedCollection<Comment> getAllComments(Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Get All Comments Order By {}. Page number {}, Page Size {}", sortCriteria, pageNumber, pageSize);

        return queryComments("", enabled, sortCriteria, pageNumber, pageSize, null);
    }

    @Override
    public PaginatedCollection<Comment> findCommentChildren(Comment comment, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Comment {} First Level Children Order By {}. Page number {}, Page Size {}", comment.getId(), sortCriteria, pageNumber, pageSize);

        return queryComments(
                "WHERE coalesce(" + COMMENTS + ".parent_id, 0) = ?",
                enabled, sortCriteria, pageNumber, pageSize, new Object[]{ comment.getId() });
    }

    @Override
    public PaginatedCollection<Comment> findPostChildrenComments(Post post, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Post Children Comments {} Order By {}. Page number {}, Page Size {}", post.getId(), sortCriteria, pageNumber, pageSize);

        return queryComments(
                "WHERE " + COMMENTS + ".post_id = ? AND " + COMMENTS + ".parent_id IS NULL",
                enabled, sortCriteria, pageNumber, pageSize, new Object[]{ post.getId() });
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByPost(Post post, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Comments By Post {} Order By {}. Page number {}, Page Size {}", post.getId(), sortCriteria, pageNumber, pageSize);

        return queryComments(
                "WHERE " + COMMENTS + ".post_id = ?",
                enabled, sortCriteria, pageNumber, pageSize, new Object[]{ post.getId() });
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByUser(User user, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Comments By User {} Order By {}. Page number {}, Page Size {}", user.getId(), sortCriteria, pageNumber, pageSize);

        return queryComments(
                "WHERE " + COMMENTS + ".user_id = ?",
                enabled, sortCriteria, pageNumber, pageSize, new Object[]{ user.getId() });
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

    private String addEnabledFilter(String nativeWhereStatement, Boolean enabled) {
        if(enabled == null)
            return nativeWhereStatement;

        final StringBuilder sb;

        if(nativeWhereStatement != null)
            sb = new StringBuilder(nativeWhereStatement.trim());
        else
            sb = new StringBuilder();

        if(sb.length() == 0)
            sb.append("WHERE ");
        else
            sb.append(" AND ");

        if(enabled)
            sb.append(NATIVE_ENABLED_FILTER);
        else
            sb.append(NATIVE_DISABLED_FILTER);

        return sb.toString();
    }

    private PaginatedCollection<Comment> queryComments(String nativeWhereStatement, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize, Object[] params) {

        final String nativeSelect = "SELECT " + COMMENTS + ".comment_id";

        final String nativeCountSelect = "SELECT COUNT(DISTINCT " + COMMENTS + ".comment_id)";

        final String nativeFrom = buildNativeFromStatement();

        final String nativeWhere = addEnabledFilter(nativeWhereStatement, enabled);

        final String nativeOrderBy = buildNativeOrderByStatement(sortCriteria);

        final String HQLOrderBy = buildHQLOrderByStatement(sortCriteria);

        final String nativePagination = buildNativePaginationStatement(pageNumber, pageSize);

        final String nativeCountQuery = String.format("%s %s %s", nativeCountSelect, nativeFrom, nativeWhere);

        final String nativeQuery = String.format("%s %s %s %s %s",
                nativeSelect, nativeFrom, nativeWhere, nativeOrderBy, nativePagination);

        final String fetchQuery = String.format(
                "SELECT c, sum(coalesce(likes.value, 0)) AS totalLikes " +
                        "FROM Comment c LEFT OUTER JOIN c.votes likes " +
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

            tuple.get(0, Comment.class).setTotalVotes(tuple.get(1, Long.class));
            return tuple.get(0, Comment.class);

        }).collect(Collectors.toList());

        return new PaginatedCollection<>(comments, pageNumber, pageSize, totalComments);
    }

    @Override
    public PaginatedCollection<CommentVote> getCommentVotes(Comment comment, int pageNumber, int pageSize) {

        final String nativeSelect = "SELECT " + COMMENTS_LIKES + ".comments_likes_id";

        final String nativeCountSelect = "SELECT COUNT(DISTINCT " + COMMENTS_LIKES + ".comments_likes_id)";

        final String nativeFrom = "FROM " + COMMENTS_LIKES;

        final String nativeWhere = "WHERE " + COMMENTS_LIKES + ".comment_id = :comment_id";

        final String nativeOrderBy = "ORDER BY " + COMMENTS_LIKES + ".comments_likes_id";

        final String nativePagination = buildNativePaginationStatement(pageNumber, pageSize);

        final String nativeCountQuery = String.format("%s %s %s", nativeCountSelect, nativeFrom, nativeWhere);

        final String nativeQuery = String.format("%s %s %s %s %s",
                nativeSelect, nativeFrom, nativeWhere, nativeOrderBy, nativePagination);

        final String fetchQuery = "SELECT cv FROM CommentVote cv WHERE cv.id IN :commentVoteIds ORDER BY cv.id";

        LOGGER.debug("QueryCommentVotes nativeCountQuery: {}", nativeCountQuery);
        LOGGER.debug("QueryCommentVotes nativeQuery: {}", nativeQuery);
        LOGGER.debug("QueryCommentVotes fetchQuery: {}", fetchQuery);

        final long totalCommentVotes =
                ((Number) em.createNativeQuery(nativeCountQuery)
                        .setParameter("comment_id", comment.getId())
                        .getSingleResult())
                        .longValue();

        if(totalCommentVotes == 0) {
            LOGGER.debug("QueryCommentVotes Total Count == 0");
            return new PaginatedCollection<>(Collections.emptyList(), pageNumber, pageSize, totalCommentVotes);
        }

        @SuppressWarnings("unchecked")
        final Collection<Long> commentVoteIds =
                ((List<Number>) em.createNativeQuery(nativeQuery).setParameter("comment_id", comment.getId()).getResultList())
                        .stream().map(Number::longValue).collect(Collectors.toList());

        if(commentVoteIds.isEmpty()) {
            LOGGER.debug("QueryCommentVotes Empty Page");
            return new PaginatedCollection<>(Collections.emptyList(), pageNumber, pageSize, totalCommentVotes);
        }

        final Collection<CommentVote> results = em.createQuery(fetchQuery, CommentVote.class)
                .setParameter("commentVoteIds", commentVoteIds).getResultList();

        return new PaginatedCollection<>(results, pageNumber, pageSize, totalCommentVotes);
    }
}
