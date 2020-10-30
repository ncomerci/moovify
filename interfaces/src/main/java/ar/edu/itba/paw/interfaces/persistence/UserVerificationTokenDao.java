package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerificationToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserVerificationTokenDao {

    UserVerificationToken createVerificationToken(String token, LocalDateTime expiryDate, User user);

    Optional<UserVerificationToken> getVerificationToken(String token);

    Optional<UserVerificationToken> findVerificationTokenByUser(User user);

    void deleteVerificationToken(UserVerificationToken token);
}
