package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerificationToken;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface UserDao {

    User register(String username, String password, String name, String email, Collection<String> roleNames);

    void enableUser(final long userId, final String fullAccessRole, final String notValidatedRole);

    boolean userHasRole(long userId, String role);

    Collection<Role> addRoles(long userId, Collection<String> roleNames);

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Collection<User> searchUsers(String query);

    Collection<User> getAllUsers();

    long createVerificationToken(String token, LocalDateTime expiryTimestamp, long userId);

    Optional<UserVerificationToken> getVerificationToken(String token);
}
