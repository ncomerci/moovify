package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PasswordResetTokenDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.UserVerificationTokenDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URISyntaxException;
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
    private static final String NOT_VALIDATED_ROLE = "NOT_VALIDATED";
    private static final String USER_ROLE = "USER";

    private static final long DEFAULT_AVATAR_ID = 0;
    private static final String DEFAULT_AVATAR_PATH = "/images/avatar.jpg";
    private static final String AVATAR_SECURITY_TAG = "AVATAR";

    @Override
    public User register(String username, String password, String name, String email, String description, byte[] avatar, String confirmationMailTemplate) {

        final Long avatarId = (avatar.length == 0)? null : imageService.uploadImage(avatar, AVATAR_SECURITY_TAG);

        final User user = userDao.register(username, passwordEncoder.encode(password),
                name, email, description, Collections.singletonList(NOT_VALIDATED_ROLE), avatarId, true);

        createConfirmationEmail(user, confirmationMailTemplate);

        return user;
    }

    @Override
    public void editName(long user_id, String name) {
        userDao.editName(user_id, name);
    }

    @Override
    public void editUsername(long user_id, String username) {
        userDao.editUsername(user_id, username);
    }

    @Override
    public void editDescription(long user_id, String description) {
        userDao.editDescription(user_id, description);
    }


    @Override
    public void changePassword(long user_id, String password) {
        userDao.updatePassword(user_id, passwordEncoder.encode(password));
    }

    public Optional<byte[]> getAvatar(long avatarId) throws IOException, URISyntaxException {

        if(avatarId == DEFAULT_AVATAR_ID)
            return Optional.of(imageService.getImage(DEFAULT_AVATAR_PATH));

        else
            return imageService.getImage(avatarId, AVATAR_SECURITY_TAG);
    }

    public void updateAvatar(User user, byte[] newAvatar) {

        imageService.deleteImage(user.getAvatarId());

        final long newAvatarId = imageService.uploadImage(newAvatar, AVATAR_SECURITY_TAG);

        userDao.updateAvatarId(user.getId(), newAvatarId);
    }

    @Override
    public void createConfirmationEmail(User user, String confirmationMailTemplate) {

        final String token = UUID.randomUUID().toString();

        userVerificationTokenDao.createVerificationToken(token, UserVerificationToken.calculateExpiryDate(), user.getId());

        Map<String, Object> emailVariables = new HashMap<>();
        emailVariables.put("token", token);

        try {
            mailService.sendEmail(user.getEmail(), "Moovify - Confirmation Email", confirmationMailTemplate, emailVariables);
        }
        catch (MessagingException e) {
            // TODO: Log and rollback and handle exception better. Por ejemplo, mandarlo a Controller y mostrar un intentar enviar el mail de nuevo
            System.out.println("Confirmation email failed to send");
        }
    }

    @Override
    public void createPasswordResetEmail(User user, String passwordResetMailTemplate) {
        
        final String token = UUID.randomUUID().toString();

        passwordResetTokenDao.createPasswordResetToken(token, PasswordResetToken.calculateExpiryDate(), user.getId());

        Map<String, Object> emailVariables = new HashMap<>();
        emailVariables.put("token", token);

        try {
            mailService.sendEmail(user.getEmail(), "Moovify - Password Reset", passwordResetMailTemplate, emailVariables);
        }
        catch (MessagingException e) {
            // TODO: Log. Mejor handling del error de mail
            System.out.println("Password reset email failed to send");
        }
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
    public boolean hasUserLiked(String username, long postId) {
        return userDao.hasUserLiked(username, postId);
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
    public PaginatedCollection<User> getAllUsers(int pageNumber, int pageSize) {
        return userDao.getAllUsers(UserDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    private void replaceUserRole(User user, String newRole, String oldRole) {

        userDao.replaceUserRole(user.getId(), newRole, oldRole);

        user.getRoles().removeIf(role -> role.getRole().equals(oldRole));

        user.getRoles().add(new Role(newRole));
    }


}
