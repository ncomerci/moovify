package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PasswordResetTokenDao;
import ar.edu.itba.paw.models.PasswordResetToken;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class PasswordResetTokenDaoImpl implements PasswordResetTokenDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetTokenDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public PasswordResetToken createPasswordResetToken(String token, LocalDateTime expiryDate, User user) {

        final PasswordResetToken passwordResetToken = new PasswordResetToken(token, expiryDate, user);

        em.persist(passwordResetToken);

        LOGGER.info("Created PasswordResetToken: {}", passwordResetToken.getId());

        return passwordResetToken;
    }

    @Override
    public Optional<PasswordResetToken> getResetPasswordToken(String token) {

        LOGGER.info("Get Reset Password Token: {}", token);

        return em
                .createQuery(
                        "SELECT prt FROM PasswordResetToken prt WHERE prt.token = :token",
                        PasswordResetToken.class)
                .setParameter("token", token)
                .getResultList()
                .stream().findFirst();
    }

    @Override
    public Optional<PasswordResetToken> findPasswordTokenByUser(User user) {

        LOGGER.info("Find Password Token By User: {}", user.getId());

        return em
                .createQuery(
                        "SELECT prt FROM PasswordResetToken prt WHERE prt.user.id = :id",
                        PasswordResetToken.class)
                .setParameter("id", user.getId())
                .getResultList()
                .stream().findFirst();
    }

    @Override
    public void deletePasswordResetToken(PasswordResetToken token) {
        LOGGER.info("Delete Password Reset Token: {}", token.getId());
        em.remove(token);
    }
}