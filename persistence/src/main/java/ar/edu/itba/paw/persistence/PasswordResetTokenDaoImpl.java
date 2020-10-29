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

        return passwordResetToken;
    }

    @Override
    public Optional<PasswordResetToken> getResetPasswordToken(String token) {

        return em
                .createQuery(
                        "SELECT prt FROM PasswordResetToken prt WHERE prt.token = :token",
                        PasswordResetToken.class)
                .setParameter("token", token)
                .getResultList()
                .stream().findFirst();
    }

    @Override
    public void deletePasswordResetToken(PasswordResetToken token) {
        em.remove(token);
    }
}