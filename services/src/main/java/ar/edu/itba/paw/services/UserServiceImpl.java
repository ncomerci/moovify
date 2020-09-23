package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PasswordResetTokenDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.UserVerificationTokenDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserVerificationTokenDao userVerificationTokenDao;

    @Autowired
    private PasswordResetTokenDao passwordResetTokenDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // All users are created with NOT_VALIDATED_ROLE by default
    private static final String NOT_VALIDATED_ROLE = "NOT_VALIDATED";
    private static final String USER_ROLE = "USER";

    @Override
    public User register(String username, String password, String name, String email) {
        return userDao.register(username, passwordEncoder.encode(password), name, email, Collections.singletonList(NOT_VALIDATED_ROLE));
    }

    @Override
    public String createVerificationToken(long userId) {

        final String token = UUID.randomUUID().toString();

        userVerificationTokenDao.createVerificationToken(token, UserVerificationToken.calculateExpiryDate(), userId);

        return token;
    }

    @Override
    public String createPasswordResetToken(long userId) {
        
        final String token = UUID.randomUUID().toString();

        passwordResetTokenDao.createPasswordResetToken(token, PasswordResetToken.calculateExpiryDate(), userId);
        
        return token;
    }

    @Override
    public Optional<User> confirmRegistration(String token) {
        Optional<UserVerificationToken> optToken = userVerificationTokenDao.getVerificationToken(token);

        if(!optToken.isPresent() || !optToken.get().isValid())
            return Optional.empty();

        final User user = optToken.get().getUser();

        replaceUserRole(user, USER_ROLE, NOT_VALIDATED_ROLE);

        // Delete Token. It is not needed anymore
        userVerificationTokenDao.deleteVerificationToken(user.getId());

        return Optional.of(user);
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        return passwordResetTokenDao.getResetPasswordToken(token)
                .map(PasswordResetToken::isValid)
                .orElse(false);
    }

    @Override
    public Optional<User> updatePassword(String password, String token) {
        Optional<PasswordResetToken> optToken = passwordResetTokenDao.getResetPasswordToken(token);

        if(!optToken.isPresent() || !optToken.get().isValid())
            return Optional.empty();

        final User user = optToken.get().getUser();

        userDao.updatePassword(user.getId(), passwordEncoder.encode(password));

        // Delete Token. It is not needed anymore
        passwordResetTokenDao.deletePasswordResetToken(user.getId());

        return Optional.of(user);
    }

    @Override
    public boolean emailExistsAndIsValidated(String email) {
        return userDao.userHasRole(email, USER_ROLE);
    }

    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public Collection<Post> findPostsByUserId(long user_id) {
        return postDao.findPostsByUserId(user_id, EnumSet.noneOf(PostDao.FetchRelation.class));
    }

    @Override
    public Collection<Post> getAllUsers(long user_id) {
        return postDao.findPostsByUserId(user_id, EnumSet.noneOf(PostDao.FetchRelation.class));
    }

    @Override
    public Collection<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    private void replaceUserRole(User user, String newRole, String oldRole) {

        userDao.replaceUserRole(user.getId(), newRole, oldRole);

        user.getRoles().removeIf(role -> role.getRole().equals(oldRole));

        user.getRoles().add(new Role(newRole));
    }
}
