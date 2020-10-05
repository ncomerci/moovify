package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {

    enum SortCriteria {
        NEWEST, OLDEST, LIKES, NAME
    }

    User register(String username, String password, String name, String email, String description, Collection<String> roleNames,  Long avatarId, boolean enabled);

    void editName(long user_id, String name);

    void editUsername(long user_id, String username);

    void editDescription(long user_id, String description);

    void replaceUserRole(final long userId, final String newRole, final String oldRole);

    boolean userHasRole(long userId, String role);

    boolean userHasRole(String email, String role);

    boolean hasUserLiked(String username, long postId);

    Collection<Role> addRoles(long userId, Collection<String> roleNames);

    void updatePassword(long userId, String password);

    void updateAvatarId(long userId, long avatarId);

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    PaginatedCollection<User> getAllUsers(SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> searchUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> searchUsersByRole(String query, String role, SortCriteria sortCriteria, int pageNumber, int pageSize);
}
