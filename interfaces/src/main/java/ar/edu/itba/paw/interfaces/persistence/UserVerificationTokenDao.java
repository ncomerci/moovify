package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.UserVerificationToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserVerificationTokenDao {

    long createVerificationToken(String token, LocalDateTime expiryDate, long userId);

    Optional<UserVerificationToken> getVerificationToken(String token);

    void deleteVerificationToken(long userId);
}
