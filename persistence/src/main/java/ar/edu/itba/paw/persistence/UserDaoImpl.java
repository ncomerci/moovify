package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateEmailException;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUsernameException;
import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager em;

    private static final EnumMap<SortCriteria, String> sortCriteriaQueryMap = initializeSortCriteriaQueryMap();
    private static final EnumMap<SortCriteria, QueryBlockExecutor> sortCriteriaQueryExecutorMap = initializeSortCriteriaQueryExecutorMap();

    private static EnumMap<SortCriteria, String> initializeSortCriteriaQueryMap() {

        EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, "USERS.creation_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, "USERS.creation_date");
        sortCriteriaQuery.put(SortCriteria.LIKES, "TOTAL_LIKES.total_likes desc");
        sortCriteriaQuery.put(SortCriteria.USERNAME, "USERS.username");

        return sortCriteriaQuery;
    }

    private static EnumMap<SortCriteria, QueryBlockExecutor> initializeSortCriteriaQueryExecutorMap() {

        EnumMap<SortCriteria, QueryBlockExecutor> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, (cb, q, u, tl) -> q.orderBy(cb.desc(u.get("creationDate"))) );
        sortCriteriaQuery.put(SortCriteria.OLDEST, (cb, q, u, tl) -> q.orderBy(cb.asc(u.get("creationDate"))) );
        sortCriteriaQuery.put(SortCriteria.LIKES, (cb, q, u, tl) -> q.orderBy(cb.desc(tl)) );
        sortCriteriaQuery.put(SortCriteria.USERNAME, (cb, q, u, tl) -> q.orderBy(cb.asc(u.get("username"))) );

        return sortCriteriaQuery;
    }

    private static final PredicateSupplier<String> queryEqualsUsername =
            (cb, u, tl, query) -> cb.like(cb.lower(u.get("name")), cb.concat("%", cb.concat(cb.lower(cb.literal(query)), "%")));

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

        final Collection<User> users = queryUsers(null, sortCriteria);

        return new PaginatedCollection<>(users, pageNumber, pageSize, users.size());
    }

    @Override
    public PaginatedCollection<User> searchUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        final Collection<User> users =
                queryUsers((cb, q, u, tl) -> q.where(
                            queryEqualsUsername.get(cb, u, tl, query)

                        ), sortCriteria);

        return new PaginatedCollection<>(users, pageNumber, pageSize, users.size());
    }

    @Override
    public PaginatedCollection<User> searchUsersByRole(String query, Role role, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        final Collection<User> users =
                queryUsers(
                        (cb, q, u, tl) -> {

                            Subquery<Long> sq = q.subquery(Long.class);
                            Root<User> su = sq.from(User.class);
                            Join<User, Role> roles = su.join("roles", JoinType.INNER);
                            sq.select(su.get("id")).where(cb.literal(role.name()).in(roles));

                            q.where(
                                    queryEqualsUsername.get(cb, u, tl, query),
                                    cb.in(u.get("id")).value(sq)
                            );
                            }, sortCriteria);

        return new PaginatedCollection<>(users, pageNumber, pageSize, users.size());
    }

    @Override
    public PaginatedCollection<User> searchDeletedUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        final Collection<User> users =
                queryUsers((cb, q, u, tl) -> q.where(
                        queryEqualsUsername.get(cb, u, tl, query),
                        cb.equal(u.get("enabled"), false)

                ), sortCriteria);

        return new PaginatedCollection<>(users, pageNumber, pageSize, users.size());
    }

    private Collection<User> queryUsers(QueryBlockExecutor whereQueryBlock, SortCriteria sortCriteria) {
        // Original Query
        /*
        final Collection<Object[]> queryResult = em.createQuery(
                "SELECT u, sum(coalesce(commentLikes.value, 0) + coalesce(postLikes.value, 0)) AS totalLikes" +
                        " FROM User u LEFT OUTER JOIN u.commentLikes commentLikes LEFT OUTER JOIN u.postLikes postLikes" +
                          --- CUSTOM WHERE ---
                        " GROUP BY u
                          --- CUSTOM ORDER BY ---
                        , Object[].class)
                .getResultList();
        */

        //TODO: Falta paginacion
        /*Query pagingQuery = em.createNativeQuery("SELECT * FROM users ORDER BY user_id LIMIT "
                + String.valueOf(pageSize) + " OFFSET " + String.valueOf(pageNumber));

        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>) pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        TypedQuery<User> query = em.createQuery("select u from User u where u.id IN :idList", User.class)
                .setParameter("idList", resultList);*/

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Tuple> q = cb.createTupleQuery();

        // From And Joins
        final Root<User> u = q.from(User.class);

        final Join<User, CommentLike> commentLikes = u.join("commentLikes", JoinType.LEFT);

        final Join<User, PostLike> postLikes = u.join("postLikes", JoinType.LEFT);

        // Preparing Select Aggregated Parameter
        final Expression<Long> totalLikes = cb.sumAsLong(
                        cb.sum(
                                cb.coalesce(commentLikes.get("value"), 0),
                                cb.coalesce(postLikes.get("value"), 0)
                        )
                );

        // Select
        q.multiselect(u, totalLikes);

        // Where
        if(whereQueryBlock != null)
            whereQueryBlock.execute(cb, q, u, totalLikes);

        // Group By
        q.groupBy(u);

        // Order By
        sortCriteriaQueryExecutorMap.get(sortCriteria).execute(cb, q, u, totalLikes);

        // Execute Query
        Collection<Tuple> tuples = em.createQuery(q).getResultList();

        // Map Tuples To Users
        return tuples.stream().map(tuple -> {

            tuple.get(u).setTotalLikes(tuple.get(totalLikes));
            return tuple.get(u);

        }).collect(Collectors.toList());
    }

    private <T> Optional<User> findByCriteria(String field, T value, boolean enabled) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<User> q = cb.createQuery(User.class);

        Root<User> u = q.from(User.class);

        q.select(u);

        q.where(cb.equal(u.get(field), value), cb.equal(u.get("enabled"), enabled));

        return em.createQuery(q).getResultList().stream().findFirst();
    }

    @FunctionalInterface
    private interface PredicateSupplier<T> {

        Predicate get(CriteriaBuilder cb, Root<User> u, Expression<Long> totalLikes, T param);
    }

    @FunctionalInterface
    private interface QueryBlockExecutor {

        void execute(CriteriaBuilder cb, CriteriaQuery<Tuple> q, Root<User> u, Expression<Long> totalLikes);
    }
}
