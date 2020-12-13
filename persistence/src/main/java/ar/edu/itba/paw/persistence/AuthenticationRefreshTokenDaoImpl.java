package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.AuthenticationRefreshTokenDao;
import ar.edu.itba.paw.models.AuthenticationRefreshToken;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class AuthenticationRefreshTokenDaoImpl implements AuthenticationRefreshTokenDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationRefreshTokenDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public AuthenticationRefreshToken createRefreshToken(String token, LocalDateTime expiryDate, User user) {

        final AuthenticationRefreshToken refreshToken = new AuthenticationRefreshToken(token, expiryDate, user);

        em.persist(refreshToken);

        LOGGER.info("Created AuthenticationRefreshToken: {}", refreshToken.getId());

        return refreshToken;
    }

    @Override
    public Optional<AuthenticationRefreshToken> getRefreshToken(String token) {

        LOGGER.info("Get Authentication Refresh Token: {}", token);

        return em.createQuery(
                "SELECT art FROM AuthenticationRefreshToken art WHERE art.token = :token",
                AuthenticationRefreshToken.class)
                .setParameter("token", token)
                .getResultList()
                .stream().findFirst();
    }

    @Override
    public Optional<AuthenticationRefreshToken> findRefreshTokenByUser(User user) {

        LOGGER.info("Find Authentication Refresh Token By User: {}", user.getId());

        return em.createQuery(
                "SELECT art FROM AuthenticationRefreshToken art WHERE art.user.id = :id",
                AuthenticationRefreshToken.class)
                .setParameter("id", user.getId())
                .getResultList()
                .stream().findFirst();
    }

    @Override
    public void deleteRefreshToken(AuthenticationRefreshToken token) {
        LOGGER.info("Delete Authentication Reset Token: {}", token.getId());
        em.remove(token);
    }
}
