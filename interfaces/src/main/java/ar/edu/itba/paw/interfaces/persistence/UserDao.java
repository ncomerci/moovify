package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {

    User register(String username, String password, String name, String email, Collection<String> roleNames);

    void replaceUserRole(final long userId, final String newRole, final String oldRole);

    boolean userHasRole(long userId, String role);

    boolean userHasRole(String email, String role);

    boolean hasUserLiked(String username, long postId);

    Collection<Role> addRoles(long userId, Collection<String> roleNames);

    void updatePassword(long userId, String password);

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Collection<User> searchUsers(String query);


    Collection<User> getAllUsers();
}
