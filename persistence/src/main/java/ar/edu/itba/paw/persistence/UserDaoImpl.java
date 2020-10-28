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
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager em;

//    TODO: ver como hacer para chequear el unique email y el unique username
    @Override
    public User register(String username, String password, String name, String email, String description, Collection<Role> roleNames, Image avatar, boolean enabled) throws DuplicateEmailException, DuplicateUsernameException {

        final User user = new User(LocalDateTime.now(), username, password, name, email, description, avatar, roleNames, enabled, Collections.emptyList(), Collections.emptyList());

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
        List<User> users = em.createQuery("SELECT u FROM User u ORDER BY u.username", User.class).getResultList();

        return new PaginatedCollection<>(users.subList(pageNumber * pageSize, (pageNumber + 1) * pageSize), pageNumber, pageSize, users.size());
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

    private Optional<User> findByCriteria(String field, Object fieldValue, boolean enabled) {
        TypedQuery<User> query = em.createQuery("FROM User u WHERE u." + field + " = :fieldValue AND u.enabled = :enabled", User.class)
                .setParameter("fieldValue", fieldValue)
                .setParameter("enabled", enabled);

        return Optional.of(query.getSingleResult());
    }
}
