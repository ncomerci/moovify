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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

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
    }

    @Transactional
    public Optional<byte[]> getAvatar(long avatarId) {

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
    }

    @Transactional
    @Override
    public void createConfirmationEmail(User user, String confirmationMailTemplate) {

        final String token = UUID.randomUUID().toString();

        userVerificationTokenDao.createVerificationToken(token, UserVerificationToken.calculateExpiryDate(), user);

        Map<String, Object> emailVariables = new HashMap<>();
        emailVariables.put("token", token);

        mailService.sendEmail(user.getEmail(), "Moovify - Confirmation Email", confirmationMailTemplate, emailVariables);
    }

    @Transactional
    @Override
    public void createPasswordResetEmail(User user, String passwordResetMailTemplate) {
        
        final String token = UUID.randomUUID().toString();

        passwordResetTokenDao.createPasswordResetToken(token, PasswordResetToken.calculateExpiryDate(), user);

        Map<String, Object> emailVariables = new HashMap<>();
        emailVariables.put("token", token);

        mailService.sendEmail(user.getEmail(), "Moovify - Password Reset", passwordResetMailTemplate, emailVariables);
    }

    @Transactional
    @Override
    public Optional<User> confirmRegistration(String token) {

        Optional<UserVerificationToken> optToken = userVerificationTokenDao.getVerificationToken(token);

        if(!optToken.isPresent() || !optToken.get().isValid())
            return Optional.empty();

        final User user = optToken.get().getUser();

        replaceUserRole(user, Role.USER_ROLE, Role.NOT_VALIDATED_ROLE);

        // Delete Token. It is not needed anymore
        userVerificationTokenDao.deleteVerificationToken(user);

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

        Optional<PasswordResetToken> optToken = passwordResetTokenDao.getResetPasswordToken(token);

        if(!optToken.isPresent() || !optToken.get().isValid())
            return Optional.empty();

        final User user = optToken.get().getUser();

        userDao.updatePassword(user, passwordEncoder.encode(password));

        // Delete Token. It is not needed anymore
        passwordResetTokenDao.deletePasswordResetToken(user);

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
