package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.PasswordResetToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenDao {

    long createPasswordResetToken(String token, LocalDateTime expiryDate, long userId);

    Optional<PasswordResetToken> getResetPasswordToken(String token);

    void deletePasswordResetToken(long userId);
}
