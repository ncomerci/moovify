package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserVerificationTokenDao;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerificationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class UserVerificationTokenDaoImpl implements UserVerificationTokenDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserVerificationTokenDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public UserVerificationToken createVerificationToken(String token, LocalDateTime expiryDate, User user) {

        final UserVerificationToken userVerificationToken = new UserVerificationToken(token, expiryDate, user);

        em.persist(userVerificationToken);

        LOGGER.info("Created UserVerificationToken: {}", userVerificationToken.getId());

        return userVerificationToken;
    }

    @Override
    public Optional<UserVerificationToken> getVerificationToken(String token) {

        LOGGER.info("Get User Verification Token: {}", token);

        return em.createQuery(
                        "SELECT uvt FROM UserVerificationToken uvt WHERE uvt.token = :token",
                        UserVerificationToken.class)
                .setParameter("token", token)
                .getResultList()
                .stream().findFirst();
    }

    @Override
    public Optional<UserVerificationToken> findVerificationTokenByUser(User user) {

        LOGGER.info("Find User Verification Token By User: {}", user.getId());

        return em.createQuery(
                 "SELECT uvt FROM UserVerificationToken uvt WHERE uvt.user.id = :id",
                 UserVerificationToken.class)
                .setParameter("id", user.getId())
                .getResultList()
                .stream().findFirst();
    }

    @Override
    public void deleteVerificationToken(UserVerificationToken token) {
        LOGGER.info("Delete User Verification Reset Token: {}", token.getId());
        em.remove(token);
    }
}