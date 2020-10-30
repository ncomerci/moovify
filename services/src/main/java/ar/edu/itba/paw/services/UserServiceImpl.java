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

        final Image image = imageService.uploadImage(avatar, AVATAR_SECURITY_TAG);

        final User user = userDao.register(username, passwordEncoder.encode(password),
                name, email, description, Collections.singletonList(Role.NOT_VALIDATED), image, true);

        createConfirmationEmail(user, confirmationMailTemplate);

        LOGGER.info("Created User {}", user.getId());

        return user;
    }

    @Transactional
    @Override
    public void updateName(User user, String name) {
        user.setName(name);
    }

    @Transactional
    @Override
    public void updateUsername(User user, String username) throws DuplicateUsernameException {
        user.setName(username);
    }

    @Transactional
    @Override
    public void updateDescription(User user, String description) {
        user.setDescription(description);
    }

    @Transactional
    @Override
    public void updatePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
    }

    @Transactional
    @Override
    public void updateAvatar(User user, byte[] newAvatar) {

        Objects.requireNonNull(user);

        final Image avatar = imageService.uploadImage(newAvatar, AVATAR_SECURITY_TAG);

        user.setAvatar(avatar);

        imageService.deleteImage(user.getAvatarId());

        LOGGER.info("User's {} Avatar was Updated to {}", user.getId(), avatar == null ? 0 : avatar.getId());
    }

    @Transactional
    @Override
    public Optional<byte[]> getAvatar(long avatarId) {

        LOGGER.info("Accessing avatar {}. (Default {})", avatarId, avatarId == User.DEFAULT_AVATAR_ID);

        if(avatarId == User.DEFAULT_AVATAR_ID)
            return Optional.of(imageService.getImage(DEFAULT_AVATAR_PATH));

        else
            return imageService.getImage(avatarId, AVATAR_SECURITY_TAG);
    }

    @Transactional
    @Override
    public void deleteUser(User user) {
        user.setEnabled(false);
    }

    @Transactional
    @Override
    public void restoreUser(User user) {
        user.setEnabled(true);
    }

    @Transactional
    @Override
    public void promoteUserToAdmin(User user) {

        Objects.requireNonNull(user);

        user.getRoles().add(Role.ADMIN);

        LOGGER.info("Promoted User {} to Admin", user.getId());
    }

    @Transactional
    @Override
    public void createConfirmationEmail(User user, String confirmationMailTemplate) {

        Objects.requireNonNull(user);

        final String token = UUID.randomUUID().toString();

        final Optional<UserVerificationToken> optToken = userVerificationTokenDao.findVerificationTokenByUser(user);

        if(optToken.isPresent()) {
            optToken.get().setToken(token);
            optToken.get().resetExpiryDate();
        }

        else
            userVerificationTokenDao.createVerificationToken(token, UserVerificationToken.calculateExpiryDate(), user);

        final Map<String, Object> emailVariables = new HashMap<>();

        emailVariables.put("token", token);

        mailService.sendEmail(user.getEmail(), "Moovify - Confirmation Email", confirmationMailTemplate, emailVariables);

        LOGGER.info("Created and sent email confirmation token {} to User {}", token, user.getId());
    }

    @Transactional
    @Override
    public void createPasswordResetEmail(User user, String passwordResetMailTemplate) {

        Objects.requireNonNull(user);
        
        final String token = UUID.randomUUID().toString();

        final Optional<PasswordResetToken> optToken = passwordResetTokenDao.findPasswordTokenByUser(user);

        if(optToken.isPresent()) {
            optToken.get().setToken(token);
            optToken.get().resetExpiryDate();
        }
        else
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

        final UserVerificationToken userVerificationToken = optToken.get();

        final User user = userVerificationToken.getUser();

        replaceUserRole(user, Role.USER, Role.NOT_VALIDATED);

        // Delete Token. It is not needed anymore
        userVerificationTokenDao.deleteVerificationToken(userVerificationToken);

        LOGGER.info("User {} has confirmed their email", user.getId());

        return Optional.of(user);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean validatePasswordResetToken(String token) {
        return passwordResetTokenDao.getResetPasswordToken(token)
                .map(resetToken -> resetToken.isValid() && resetToken.getUser().isEnabled())
                .orElse(false);
    }

    @Transactional
    @Override
    public Optional<User> updatePassword(String password, String token) {

        final Optional<PasswordResetToken> optToken = passwordResetTokenDao.getResetPasswordToken(token);

        if(!optToken.isPresent() || !optToken.get().isValid()) {
            LOGGER.warn("A user tried to update their password, but it's token {} was invalid", token);
            return Optional.empty();
        }

        final PasswordResetToken passwordResetToken = optToken.get();

        final User user = passwordResetToken.getUser();

        final String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);

        // Delete Token. It is not needed anymore
        passwordResetTokenDao.deletePasswordResetToken(passwordResetToken);

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
    public Optional<User> findDeletedUserById(long id) {
        return userDao.findDeletedUserById(id);
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

    private void replaceUserRole(User user, Role newRole, Role oldRole) {

        user.getRoles().removeIf(role -> role.equals(oldRole));

        user.getRoles().add(newRole);
    }
}
