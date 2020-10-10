package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PasswordResetTokenDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.UserVerificationTokenDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateEmailException;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUsernameException;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private MailService mailService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserVerificationTokenDao userVerificationTokenDao;

    @Autowired
    private PasswordResetTokenDao passwordResetTokenDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // All users are created with NOT_VALIDATED_ROLE by default

    private static final String DEFAULT_AVATAR_PATH = "/images/avatar.jpg";
    private static final String AVATAR_SECURITY_TAG = "AVATAR";

    @Transactional
    @Override
    public User register(String username, String password, String name, String email, String description, byte[] avatar, String confirmationMailTemplate) throws DuplicateUsernameException, DuplicateEmailException {

        final Long avatarId = (avatar.length == 0)? null : imageService.uploadImage(avatar, AVATAR_SECURITY_TAG);

        final User user = userDao.register(username, passwordEncoder.encode(password),
                name, email, description, Collections.singletonList(Role.NOT_VALIDATED_ROLE), avatarId, true);

        createConfirmationEmail(user, confirmationMailTemplate);

        LOGGER.info("Created User {}", user.getId());

        return user;
    }

    @Transactional
    @Override
    public void updateName(User user, String name) {
        userDao.updateName(user, name);
    }

    @Transactional
    @Override
    public void updateUsername(User user, String username) throws DuplicateUsernameException {
        userDao.updateUsername(user, username);
    }

    @Transactional
    @Override
    public void updateDescription(User user, String description) {
        userDao.updateDescription(user, description);
    }

    @Transactional
    @Override
    public void updatePassword(User user, String password) {
        userDao.updatePassword(user, passwordEncoder.encode(password));
    }

    @Transactional
    @Override
    public void updateAvatar(User user, byte[] newAvatar) {

        final long newAvatarId = imageService.uploadImage(newAvatar, AVATAR_SECURITY_TAG);

        userDao.updateAvatarId(user, newAvatarId);

        imageService.deleteImage(user.getAvatarId());

        LOGGER.info("User's {} Avatar was Updated to {}", user.getId(), newAvatarId);
    }

    @Transactional
    public Optional<byte[]> getAvatar(long avatarId) {

        LOGGER.info("Accessing avatar {}. (Default {})", avatarId, avatarId == User.DEFAULT_AVATAR_ID);

        if(avatarId == User.DEFAULT_AVATAR_ID)
            return Optional.of(imageService.getImage(DEFAULT_AVATAR_PATH));

        else
            return imageService.getImage(avatarId, AVATAR_SECURITY_TAG);
    }

    @Transactional
    @Override
    public void deleteUser(User userId) {
        userDao.deleteUser(userId);
    }

    @Transactional
    @Override
    public void restoreUser(User user) {
        userDao.restoreUser(user);
    }

    @Transactional
    @Override
    public void promoteUserToAdmin(User user) {

        userDao.addRoles(user, Collections.singletonList(Role.ADMIN_ROLE));

        user.getRoles().add(new Role(Role.ADMIN_ROLE));

        LOGGER.info("Promoted User {} to Admin", user.getId());
    }

    @Transactional
    @Override
    public void createConfirmationEmail(User user, String confirmationMailTemplate) {

        final String token = UUID.randomUUID().toString();

        userVerificationTokenDao.createVerificationToken(token, UserVerificationToken.calculateExpiryDate(), user);

        final Map<String, Object> emailVariables = new HashMap<>();

        emailVariables.put("token", token);

        mailService.sendEmail(user.getEmail(), "Moovify - Confirmation Email", confirmationMailTemplate, emailVariables);

        LOGGER.info("Created and sent email confirmation token {} to User {}", token, user.getId());
    }

    @Transactional
    @Override
    public void createPasswordResetEmail(User user, String passwordResetMailTemplate) {
        
        final String token = UUID.randomUUID().toString();

        passwordResetTokenDao.createPasswordResetToken(token, PasswordResetToken.calculateExpiryDate(), user);

        final Map<String, Object> emailVariables = new HashMap<>();
        emailVariables.put("token", token);

        mailService.sendEmail(user.getEmail(), "Moovify - Password Reset", passwordResetMailTemplate, emailVariables);

        LOGGER.info("Created and sent email confirmation token {} to User {}", token, user.getId());
    }

    @Transactional
    @Override
    public Optional<User> confirmRegistration(String token) {

        final Optional<UserVerificationToken> optToken = userVerificationTokenDao.getVerificationToken(token);

        if(!optToken.isPresent() || !optToken.get().isValid()) {
            LOGGER.warn("A user tried to confirm their email, but it's token {} was invalid", token);
            return Optional.empty();
        }

        final User user = optToken.get().getUser();

        replaceUserRole(user, Role.USER_ROLE, Role.NOT_VALIDATED_ROLE);

        // Delete Token. It is not needed anymore
        userVerificationTokenDao.deleteVerificationToken(user);

        LOGGER.info("User {} has confirmed their email", user.getId());

        return Optional.of(user);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean validatePasswordResetToken(String token) {
        return passwordResetTokenDao.getResetPasswordToken(token)
                .map(PasswordResetToken::isValid)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    @Override
    public int hasUserLikedPost(User user, Post post) {
        return userDao.hasUserLiked(user, post);
    }

    @Transactional
    @Override
    public Optional<User> updatePassword(String password, String token) {

        final Optional<PasswordResetToken> optToken = passwordResetTokenDao.getResetPasswordToken(token);

        if(!optToken.isPresent() || !optToken.get().isValid()) {
            LOGGER.warn("A user tried to update their password, but it's token {} was invalid", token);
            return Optional.empty();
        }

        final User user = optToken.get().getUser();

        userDao.updatePassword(user, passwordEncoder.encode(password));

        // Delete Token. It is not needed anymore
        passwordResetTokenDao.deletePasswordResetToken(user);

        LOGGER.info("User {} has updated their password successfully", user.getId());

        return Optional.of(user);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findUserById(long id) {
        return userDao.findUserById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<User> getAllUsers(int pageNumber, int pageSize) {
        return userDao.getAllUsers(UserDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    private void replaceUserRole(User user, String newRole, String oldRole) {

        userDao.replaceUserRole(user, newRole, oldRole);

        user.getRoles().removeIf(role -> role.getRole().equals(oldRole));

        user.getRoles().add(new Role(newRole));
    }
}
