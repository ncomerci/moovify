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
        NEWEST, OLDEST, VOTES, USERNAME, FOLLOWERS
    }

    User register(String username, String password, String name, String email, String description, String language, Set<Role> roleNames, Image avatar, boolean enabled) throws DuplicateUniqueUserAttributeException;

    void updateUsername(User user, String username) throws DuplicateUniqueUserAttributeException;

    Optional<User> findUserById(long id);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    // TODO: Kill method
    long getFollowerCount(User user);

    PaginatedCollection<User> getAllUsers(Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> getFollowedUsers(User user, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> searchUsers(String query, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> searchUsersByRole(String query, Role role, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);
}
