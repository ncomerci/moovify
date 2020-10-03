package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {

    enum SortCriteria {
        NEWEST, OLDEST
    }

    User register(String username, String password, String name, String email, Collection<String> roleNames);

    void replaceUserRole(final long userId, final String newRole, final String oldRole);

    boolean userHasRole(long userId, String role);

    boolean userHasRole(String email, String role);

    Collection<Role> addRoles(long userId, Collection<String> roleNames);

    void updatePassword(long userId, String password);

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    PaginatedCollection<User> searchUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> getAllUsers(SortCriteria sortCriteria, int pageNumber, int pageSize);
}
