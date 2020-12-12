package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.AuthenticationRefreshToken;
import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AuthenticationRefreshTokenDao {

    AuthenticationRefreshToken createRefreshToken(String token, LocalDateTime expiryDate, User user);

    Optional<AuthenticationRefreshToken> getRefreshToken(String token);

    Optional<AuthenticationRefreshToken> findRefreshTokenByUser(User user);

    void deleteRefreshToken(AuthenticationRefreshToken token);
}
