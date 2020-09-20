package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerificationToken;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface UserDao {

    User register(String username, String password, boolean enabled, String name, String email, Collection<String> roleNames);

    void enableUser(long userId);

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Collection<User> getAllUsers();

    long createVerificationToken(String token, LocalDateTime expiryTimestamp, long userId);

    Optional<UserVerificationToken> getVerificationToken(String token);
}
