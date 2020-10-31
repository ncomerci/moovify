package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;

import java.util.Optional;
import java.util.Set;

public interface UserDao {

    enum SortCriteria {
        NEWEST, OLDEST, LIKES, USERNAME
    }

    User register(String username, String password, String name, String email, String description, Set<Role> roleNames, Image avatar, boolean enabled) throws DuplicateUniqueUserAttributeException;

    void updateUsername(User user, String username) throws DuplicateUniqueUserAttributeException;

    Optional<User> findUserById(long id);

    Optional<User> findDeletedUserById(long id);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    PaginatedCollection<User> getAllUsers(SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> searchUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> searchUsersByRole(String query, Role role, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> searchDeletedUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize);
}
