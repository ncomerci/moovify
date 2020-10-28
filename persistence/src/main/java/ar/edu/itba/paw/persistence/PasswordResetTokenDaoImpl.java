package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PasswordResetTokenDao;
import ar.edu.itba.paw.models.PasswordResetToken;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class PasswordResetTokenDaoImpl implements PasswordResetTokenDao {

    @Override
    public PasswordResetToken createPasswordResetToken(String token, LocalDateTime expiryDate, User user) {
        return null;
    }

    @Override
    public Optional<PasswordResetToken> getResetPasswordToken(String token) {
        return Optional.empty();
    }

    @Override
    public void deletePasswordResetToken(User user) {

    }
}