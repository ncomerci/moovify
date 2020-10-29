package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.InvalidPaginationArgumentException;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
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

    private static final String COMMENTS = TableNames.COMMENTS.getTableName();
    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String POST_CATEGORY = TableNames.POST_CATEGORY.getTableName();
    private static final String USERS = TableNames.USERS.getTableName();
    private static final String USER_ROLE = TableNames.USER_ROLE.getTableName();
    private static final String COMMENTS_LIKES = TableNames.COMMENTS_LIKES.getTableName();


    private static final String NATIVE_BASE_COMMENT_FROM = "FROM " + COMMENTS;

    private static final String NATIVE_TOTAL_LIKES_FROM =  " INNER JOIN ( " +
            "SELECT " + COMMENTS + ".comment_id, " + "COALESCE(SUM( " + COMMENTS_LIKES + ".value ), 0) likes " +
            "FROM " + COMMENTS +
                " LEFT OUTER JOIN " +  COMMENTS_LIKES + " ON " + COMMENTS + ".comment_id = " + COMMENTS_LIKES + ".comment_id" +
            " GROUP BY " + COMMENTS + ".comment_id" +
            ") " + COMMENTS_LIKES  + " ON " + COMMENTS + ".comment_id = " + COMMENTS_LIKES + ".comment_id";


    private static final EnumMap<SortCriteria,String> sortCriteriaQueryMap = initializeSortCriteriaQueryMap();
    private static final EnumMap<SortCriteria,String> sortCriteriaHQLMap = initializeSortCriteriaHQL();

    private static EnumMap<CommentDao.SortCriteria, String> initializeSortCriteriaQueryMap() {
        final EnumMap<CommentDao.SortCriteria, String> sortCriteriaQuery = new EnumMap<>(CommentDao.SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, "COMMENTS.creation_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, "COMMENTS.creation_date");
        sortCriteriaQuery.put(SortCriteria.HOTTEST, "COMMENTS_LIKES.total_likes desc");

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

        final Comment comment = new Comment(LocalDateTime.now(), post, parent, Collections.emptyList(), body, user, enabled, Collections.emptyList());

        em.persist(comment);

        return comment;
    }

    @Override
    public Optional<Comment> findCommentById(long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public PaginatedCollection<Comment> findCommentChildren(Comment comment, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Find Comment {} First Level Children Order By {}. Page number {}, Page Size {}", comment.getId(), sortCriteria, pageNumber, pageSize);

        return queryComments(
                "WHERE " + COMMENTS + ".parent_id = ?",
                sortCriteria, pageNumber, pageSize, new Object[]{ comment.getId() });
    }

    @Override
    public PaginatedCollection<Comment> findCommentDescendants(Comment comment, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Comment> findPostCommentDescendants(Post post, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
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

        // Calculate Total Comment Count Disregarding Pagination (To Calculate Pages Later)
        final Query totalCommentsNativeQuery = em.createNativeQuery(nativeCountQuery);

        addParamsToNativeQuery(totalCommentsNativeQuery, params);

        final long totalComments = ((Number) totalCommentsNativeQuery.getSingleResult()).longValue();

        // Calculate Which Comments To Load And Load Their Ids
        final Query commentIdsNativeQuery = em.createNativeQuery(nativeQuery);

        addParamsToNativeQuery(commentIdsNativeQuery, params);

        @SuppressWarnings("unchecked")
        final Collection<Long> commentIds =
                ((List<Number>)commentIdsNativeQuery.getResultList())
                        .stream().map(Number::longValue).collect(Collectors.toList());

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
}
