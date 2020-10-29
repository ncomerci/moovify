package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateEmailException;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUsernameException;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

    private static final EnumMap<SortCriteria,String> sortCriteriaQueryMap = initializeSortCriteriaQueryMap();
    private static final EnumMap<SortCriteria,String> sortCriteriaHQLMap = initializeSortCriteriaHQLMap();

    private static EnumMap<SortCriteria, String> initializeSortCriteriaQueryMap() {

        EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, "USERS.creation_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, "USERS.creation_date");
        sortCriteriaQuery.put(SortCriteria.LIKES, "TOTAL_LIKES.total_likes desc");
        sortCriteriaQuery.put(SortCriteria.USERNAME, "USERS.username");

        return sortCriteriaQuery;
    }

    private static EnumMap<SortCriteria, String> initializeSortCriteriaHQLMap() {

        EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, "u.creation_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, "u.creation_date");
        sortCriteriaQuery.put(SortCriteria.LIKES, "TOTAL_LIKES.total_likes desc");
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
        /*Query pagingQuery = em.createNativeQuery("SELECT * FROM users ORDER BY user_id LIMIT "
                + String.valueOf(pageSize) + " OFFSET " + String.valueOf(pageNumber));

        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>) pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        TypedQuery<User> query = em.createQuery("select u from User u where u.id IN :idList", User.class)
                .setParameter("idList", resultList);*/


        final Collection<User> users = queryUsers();

        return new PaginatedCollection<>(users, pageNumber, pageSize, users.size());
    }

    @Override
    public PaginatedCollection<User> searchUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<User> searchUsersByRole(String query, Role role, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<User> searchDeletedUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    private Collection<User> queryUsers() {
        final Collection<Object[]> queryResult = em.createQuery(
                "SELECT u, sum(commentLikes.value + postLikes.value) AS totalLikes FROM User u LEFT OUTER JOIN u.commentLikes commentLikes LEFT OUTER JOIN u.postLikes postLikes GROUP BY u ORDER BY totalLikes DESC", Object[].class)
                .getResultList();

        return queryResult.stream().map(tuple -> {
            ((User) tuple[0]).setTotalLikes((long) tuple[1]);
            return ((User) tuple[0]);
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
}
