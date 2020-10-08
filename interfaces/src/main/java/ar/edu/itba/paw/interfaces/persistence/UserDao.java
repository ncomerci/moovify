package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.interfaces.exceptions.DuplicateEmailException;
import ar.edu.itba.paw.interfaces.exceptions.DuplicateUsernameException;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {

    enum SortCriteria {
        NEWEST, OLDEST, LIKES, NAME
    }

    User register(String username, String password, String name, String email, String description, Collection<String> roleNames,  Long avatarId, boolean enabled) throws DuplicateEmailException, DuplicateUsernameException;

    void updateName(User user, String name);

    void updateUsername(User user, String username) throws DuplicateUsernameException;

    void updateDescription(User user, String description);

    void deleteUser(User user);

    void restoreUser(User user);

    void replaceUserRole(final User user, final String newRole, final String oldRole);

    boolean userHasRole(User user, String role);

    boolean userHasRole(String userEmail, String role);

    int hasUserLiked(User user, Post post);

    Collection<Role> addRoles(User user, Collection<String> roleNames);

    void updatePassword(User user, String password);

    void updateAvatarId(User user, long avatarId);

    Optional<User> findUserById(long id);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    PaginatedCollection<User> getAllUsers(SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> searchUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> searchUsersByRole(String query, String role, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> searchDeletedUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize);
}
