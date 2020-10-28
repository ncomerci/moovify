package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserVerificationTokenDao;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerificationToken;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class UserVerificationTokenDaoImpl implements UserVerificationTokenDao {

    @Override
    public UserVerificationToken createVerificationToken(String token, LocalDateTime expiryDate, User user) {
        return null;
    }

    @Override
    public Optional<UserVerificationToken> getVerificationToken(String token) {
        return Optional.empty();
    }

    @Override
    public void deleteVerificationToken(User user) {

    }
}