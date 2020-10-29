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

        return userVerificationToken;
    }

    @Override
    public Optional<UserVerificationToken> getVerificationToken(String token) {

        return em
                .createQuery(
                        "SELECT uvt FROM UserVerificationToken uvt WHERE uvt.token = :token",
                        UserVerificationToken.class)
                .setParameter("token", token)
                .getResultList()
                .stream().findFirst();
    }

    @Override
    public void deleteVerificationToken(UserVerificationToken token) {
        em.remove(token);
    }
}