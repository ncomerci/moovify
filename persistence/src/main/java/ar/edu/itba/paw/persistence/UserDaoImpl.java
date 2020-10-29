package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateEmailException;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUsernameException;
import ar.edu.itba.paw.interfaces.persistence.exceptions.InvalidPaginationArgumentException;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    private static final String USERS = TableNames.USERS.getTableName();
    private static final String USER_ROLE = TableNames.USER_ROLE.getTableName();
    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String POSTS_LIKES = TableNames.POSTS_LIKES.getTableName();
    private static final String COMMENTS_LIKES = TableNames.COMMENTS_LIKES.getTableName();
    private static final String COMMENTS = TableNames.COMMENTS.getTableName();

    private static final String NATIVE_BASE_USER_FROM = "FROM " + USERS;

    private static final String NATIVE_TOTAL_LIKES_FROM = "INNER JOIN ( " +
            "SELECT " + USERS + ".user_id, coalesce(post_likes.total_likes, 0) + coalesce(comment_likes.total_likes, 0) total_likes " +

            "FROM " + USERS +

                " LEFT OUTER JOIN ( " +
                    "SELECT " + POSTS + ".user_id, SUM(" + POSTS_LIKES + ".value) total_likes " +
                    "FROM " + POSTS +
                        " INNER JOIN " + POSTS_LIKES + " ON " + POSTS + ".post_id = " + POSTS_LIKES + ".post_id " +
                    "GROUP BY " + POSTS + ".user_id " +
                ") post_likes ON " + USERS + ".user_id = post_likes.user_id " +

                "LEFT OUTER JOIN ( " +
                    "SELECT " + COMMENTS + ".user_id, SUM(" + COMMENTS_LIKES + ".value) total_likes " +
                    "FROM " + COMMENTS +
                        " INNER JOIN " + COMMENTS_LIKES + " ON " + COMMENTS + ".comment_id = " + COMMENTS_LIKES + ".comment_id " +
                    "GROUP BY " + COMMENTS + ".user_id " +
                ") comment_likes ON " + USERS + ".user_id = comment_likes.user_id " +

            ") TOTAL_LIKES ON TOTAL_LIKES.user_id = " + USERS + ".user_id";

    // Search Query Statements
    private static final String NATIVE_SEARCH_BY_USERNAME = "LOWER(" + USERS + ".username) LIKE '%' || LOWER(?) || '%'";

    private static final String NATIVE_SEARCH_BY_ROLE = USERS + ".user_id IN ( " +
            "SELECT " + USER_ROLE + ".user_id " +
            "FROM " + USER_ROLE +
            " WHERE LOWER(" + USER_ROLE + ".role_name) = LOWER(?))";

    private static final String NATIVE_ENABLED_FILTER = USERS + ".enabled = true";

    private static final EnumMap<SortCriteria, String> sortCriteriaQueryMap = initializeSortCriteriaQueryMap();
    private static final EnumMap<SortCriteria, String> sortCriteriaHQLMap = initializeSortCriteriaHQLMap();

    private static EnumMap<SortCriteria, String> initializeSortCriteriaQueryMap() {

        EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, USERS + ".creation_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, USERS + ".creation_date");
        sortCriteriaQuery.put(SortCriteria.LIKES, "TOTAL_LIKES.total_likes desc");
        sortCriteriaQuery.put(SortCriteria.USERNAME, USERS + ".username");

        return sortCriteriaQuery;
    }

    private static EnumMap<SortCriteria, String> initializeSortCriteriaHQLMap() {

        EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, "u.creationDate desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, "u.creationDate");
        sortCriteriaQuery.put(SortCriteria.LIKES, "totalLikes desc");
        sortCriteriaQuery.put(SortCriteria.USERNAME, "u.username");

        return sortCriteriaQuery;
    }

//    TODO: ver como hacer para chequear el unique email y el unique username
    @Override
    public User register(String username, String password, String name, String email, String description, Collection<Role> roleNames, Image avatar, boolean enabled) throws DuplicateEmailException, DuplicateUsernameException {

        final User user = new User(LocalDateTime.now(), username, password, name, email, description, avatar, roleNames, enabled, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        em.persist(user);

        return user;
    }

    @Override
    public Optional<User> findUserById(long id) {
        return findByCriteria("id", id, true);
    }

    @Override
    public Optional<User> findDeletedUserById(long id) {
        return findByCriteria("id", id, false);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return findByCriteria("username", username, true);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return findByCriteria("email", email, true);
    }

    @Override
    public PaginatedCollection<User> getAllUsers(SortCriteria sortCriteria, int pageNumber, int pageSize) {

        return queryUsers("", sortCriteria, pageNumber, pageSize, null);
    }

    @Override
    public PaginatedCollection<User> searchUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Users By Name {} Order By {}. Page number {}, Page Size {}", query, sortCriteria, pageNumber, pageSize);

        return queryUsers(
                "WHERE " + NATIVE_SEARCH_BY_USERNAME +
                                    " AND " + NATIVE_ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ query });
    }

    @Override
    public PaginatedCollection<User> searchUsersByRole(String query, Role role, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Users By Name {} And Role {} Order By {}. Page number {}, Page Size {}", query, role, sortCriteria, pageNumber, pageSize);

        return queryUsers(
                "WHERE " + NATIVE_SEARCH_BY_USERNAME +
                                    " AND " + NATIVE_SEARCH_BY_ROLE +
                                    " AND " + NATIVE_ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ query, role.name() });
    }

    @Override
    public PaginatedCollection<User> searchDeletedUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        return queryUsers(
                "WHERE " + NATIVE_SEARCH_BY_USERNAME +
                                " AND " + USERS + ".enabled = false",
                sortCriteria, pageNumber, pageSize, new Object[]{ query });
    }

    private String buildNativeFromStatement() {
        return NATIVE_BASE_USER_FROM + " " + NATIVE_TOTAL_LIKES_FROM;
    }

    private String buildNativeOrderByStatement(SortCriteria sortCriteria) {

        if(!sortCriteriaQueryMap.containsKey(sortCriteria)) {
            LOGGER.error("SortCriteria Native implementation not found for {} in UserDaoImpl", sortCriteria);
            throw new IllegalArgumentException();
        }

        return "ORDER BY " + sortCriteriaQueryMap.get(sortCriteria);
    }

    private String buildHQLOrderByStatement(SortCriteria sortCriteria) {

        if(!sortCriteriaHQLMap.containsKey(sortCriteria)) {
            LOGGER.error("SortCriteria HQL implementation not found for {} in UserDaoImpl", sortCriteria);
            throw new IllegalArgumentException();
        }

        return "ORDER BY " + sortCriteriaHQLMap.get(sortCriteria);
    }

    private String buildNativePaginationStatement(int pageNumber, int pageSize) {

        if(pageNumber < 0 || pageSize <= 0) {
            LOGGER.error("Invalid pagination argument found in UserDaoImpl. pageSize: {}, pageNumber: {}", pageSize, pageNumber);
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

    private PaginatedCollection<User> queryUsers(String nativeWhereStatement, SortCriteria sortCriteria, int pageNumber, int pageSize, Object[] params) {

        final String nativeSelect = "SELECT " + USERS + ".user_id";

        final String nativeCountSelect = "SELECT COUNT(DISTINCT " + USERS + ".user_id)";

        final String nativeFrom = buildNativeFromStatement();

        final String nativeOrderBy = buildNativeOrderByStatement(sortCriteria);

        final String HQLOrderBy = buildHQLOrderByStatement(sortCriteria);

        final String nativePagination = buildNativePaginationStatement(pageNumber, pageSize);

        final String nativeCountQuery = String.format("%s %s %s", nativeCountSelect, nativeFrom, nativeWhereStatement);

        final String nativeQuery = String.format("%s %s %s %s %s",
                nativeSelect, nativeFrom, nativeWhereStatement, nativeOrderBy, nativePagination);

        final String fetchQuery = String.format(
                "SELECT u, sum(coalesce(commentLikes.value, 0) + coalesce(postLikes.value, 0)) AS totalLikes " +
                "FROM User u LEFT OUTER JOIN u.commentLikes commentLikes LEFT OUTER JOIN u.postLikes postLikes " +
                "WHERE u.id IN :userIds " +
                "GROUP BY u " +
                "%s", HQLOrderBy);

        // Calculate Total User Count Disregarding Pagination (To Calculate Pages Later)
        final Query totalUsersNativeQuery = em.createNativeQuery(nativeCountQuery);

        addParamsToNativeQuery(totalUsersNativeQuery, params);

        final long totalUsers = ((Number) totalUsersNativeQuery.getSingleResult()).longValue();

        // Calculate Which Users To Load And Load Their Ids
        final Query userIdsNativeQuery = em.createNativeQuery(nativeQuery);

        addParamsToNativeQuery(userIdsNativeQuery, params);

        @SuppressWarnings("unchecked")
        final Collection<Long> userIds =
                ((List<Number>)userIdsNativeQuery.getResultList())
                .stream().map(Number::longValue).collect(Collectors.toList());

        // Get Users Based on Ids
        final Collection<Tuple> fetchQueryResult = em.createQuery(fetchQuery, Tuple.class)
                .setParameter("userIds", userIds)
                .getResultList();

        // Map Tuples To Users
        final Collection<User> users = fetchQueryResult.stream().map(tuple -> {

            tuple.get(0, User.class).setTotalLikes(tuple.get(1, Long.class));
            return tuple.get(0, User.class);

        }).collect(Collectors.toList());

        return new PaginatedCollection<>(users, pageNumber, pageSize, totalUsers);
    }

    private <T> Optional<User> findByCriteria(String field, T value, boolean enabled) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<User> q = cb.createQuery(User.class);

        Root<User> u = q.from(User.class);

        q.select(u);

        q.where(cb.equal(u.get(field), value), cb.equal(u.get("enabled"), enabled));

        return em.createQuery(q).getResultList().stream().findFirst();
    }
}
