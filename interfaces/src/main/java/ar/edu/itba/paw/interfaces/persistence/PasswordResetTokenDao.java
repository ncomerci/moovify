package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.PasswordResetToken;
import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenDao {

    PasswordResetToken createPasswordResetToken(String token, LocalDateTime expiryDate, User user);

    Optional<PasswordResetToken> getResetPasswordToken(String token);

    Optional<PasswordResetToken> findPasswordTokenByUser(User user);

    void deletePasswordResetToken(PasswordResetToken token);
}
