package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PasswordResetTokenDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.UserVerificationTokenDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.interfaces.services.exceptions.*;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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

    @Autowired
    private MessageSource messageSource;

    // All users are created with NOT_VALIDATED_ROLE by default

    private static final String DEFAULT_AVATAR_PATH = "/images/avatar.jpg";

    private final static Map<String, UserDao.SortCriteria> sortCriteriaMap = initializeSortCriteriaMap();

    private static Map<String, UserDao.SortCriteria> initializeSortCriteriaMap() {
        final Map<String, UserDao.SortCriteria> sortCriteriaMap = new LinkedHashMap<>();

        sortCriteriaMap.put("username", UserDao.SortCriteria.USERNAME);
        sortCriteriaMap.put("newest", UserDao.SortCriteria.NEWEST);
        sortCriteriaMap.put("oldest", UserDao.SortCriteria.OLDEST);
        sortCriteriaMap.put("votes", UserDao.SortCriteria.VOTES);
        sortCriteriaMap.put("followers", UserDao.SortCriteria.FOLLOWERS);

        return sortCriteriaMap;
    }

    @Transactional
    @Override
    public User register(String username, String password, String name, String email, String description, String confirmationMailTemplate) throws DuplicateUniqueUserAttributeException {

        final User user = userDao.register(username, passwordEncoder.encode(password),
                name, email, description, LocaleContextHolder.getLocale().getLanguage(),
                Collections.singleton(Role.NOT_VALIDATED), null, true);

        createConfirmationEmail(user, confirmationMailTemplate);

        LOGGER.info("Created User {}", user.getId());

        return user;
    }

    @Transactional
    @Override
    public void updateUser(User user, String name, String username, String description, String password) throws DuplicateUniqueUserAttributeException {

        updateUsername(user, username);

        updateName(user, name);

        updateDescription(user, description);

        updatePassword(user, password);
    }

    @Transactional
    @Override
    public void updateName(User user, String name) {
        if(!user.getName().equals(name))
            user.setName(name);
    }

    @Transactional
    @Override
    public void updateUsername(User user, String username) throws DuplicateUniqueUserAttributeException {
        if(!user.getUsername().equals(username))
            userDao.updateUsername(user, username);
    }

    @Transactional
    @Override
    public void updateDescription(User user, String description) {
        if(!user.getDescription().equals(description))
            user.setDescription(description);
    }

    @Transactional
    @Override
    public void updatePassword(User user, String password) {

        final String encodedPassword = passwordEncoder.encode(password);

        if(!user.getPassword().equals(encodedPassword))
            user.setPassword(encodedPassword);
    }

    @Transactional
    @Override
    public void updateAvatar(User user, byte[] newAvatar, String type) {

        Image avatar = null;

        if(newAvatar.length > 0)
            avatar = imageService.uploadImage(newAvatar, type);

        user.setAvatar(avatar);

        LOGGER.info("User's {} Avatar was Updated to {}", user.getId(), avatar == null ? 0 : avatar.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<byte[]> getAvatar(User user) {

        final long avatarId = user.getAvatarId();

        LOGGER.info("Accessing avatar {}. (Default {})", avatarId, avatarId == User.DEFAULT_AVATAR_ID);

        final Optional<byte[]> avatar;

        if(avatarId == User.DEFAULT_AVATAR_ID) {
            avatar = imageService.findImageByPath(DEFAULT_AVATAR_PATH);

            if(!avatar.isPresent())
                throw new RuntimeException("Failed loading user avatar");
        }

        else
            avatar = imageService.findImageById(avatarId);

        return avatar;
    }

    @Transactional
    @Override
    public void deleteUser(User user) throws DeletedDisabledModelException {

        if(!user.isEnabled())
            throw new DeletedDisabledModelException();

        user.setEnabled(false);
    }

    @Transactional
    @Override
    public void restoreUser(User user) throws RestoredEnabledModelException {

        if(user.isEnabled())
            throw new RestoredEnabledModelException();

        user.setEnabled(true);
    }

    @Transactional
    @Override
    public void promoteUserToAdmin(User user) throws InvalidUserPromotionException {

        if(!user.isEnabled() || user.isAdmin() || !user.isValidated())
            throw new InvalidUserPromotionException();

        user.addRole(Role.ADMIN);

        LOGGER.info("Promoted User {} to Admin", user.getId());
    }

    @Transactional
    @Override
    public void followUser(User user, User userFollowed) throws IllegalUserFollowException {

        if(!userFollowed.isEnabled())
            throw new IllegalUserFollowException();

        user.followUser(userFollowed);
    }

    @Transactional
    @Override
    public void unfollowUser(User user, User userUnfollowed) throws IllegalUserUnfollowException {

        if(!userUnfollowed.isEnabled())
            throw new IllegalUserUnfollowException();

        user.unfollowUser(userUnfollowed);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isFollowingUser(User user, User other) {

        return user.isEnabled() && other.isEnabled() && user.isUserFollowing(other);
    }


    @Transactional
    @Override
    public void createConfirmationEmail(User user, String confirmationMailTemplate) {

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

        final Locale locale = LocaleContextHolder.getLocale();

        mailService.sendEmail(user.getEmail(), messageSource.getMessage("mail.confirmation.subject", null, locale), confirmationMailTemplate, emailVariables, locale);

        LOGGER.info("Created and sent email confirmation token {} to User {}", token, user.getId());
    }

    @Transactional
    @Override
    public void createPasswordResetEmail(User user, String passwordResetMailTemplate) {

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

        final Locale locale = LocaleContextHolder.getLocale();

        mailService.sendEmail(user.getEmail(), messageSource.getMessage("mail.passwordReset.subject", null, locale), passwordResetMailTemplate, emailVariables, locale);

        LOGGER.info("Created and sent email confirmation token {} to User {}", token, user.getId());
    }

    @Transactional
    @Override
    public User confirmRegistration(String token) throws InvalidEmailConfirmationTokenException {

        final Optional<UserVerificationToken> optToken = userVerificationTokenDao.getVerificationToken(token);

        if(!optToken.isPresent() || !optToken.get().isValid())
            throw new InvalidEmailConfirmationTokenException();

        final UserVerificationToken userVerificationToken = optToken.get();

        final User user = userVerificationToken.getUser();

        user.removeRole(Role.NOT_VALIDATED);

        user.getRoles().add(Role.USER);

        // Delete Token. It is not needed anymore
        userVerificationTokenDao.deleteVerificationToken(userVerificationToken);

        LOGGER.info("User {} has confirmed their email", user.getId());

        return user;
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
    public User updatePassword(String password, String token) throws InvalidResetPasswordToken {

        final Optional<PasswordResetToken> optToken = passwordResetTokenDao.getResetPasswordToken(token);

        if(!optToken.isPresent() || !optToken.get().isValid())
            throw new InvalidResetPasswordToken();

        final PasswordResetToken passwordResetToken = optToken.get();

        final User user = passwordResetToken.getUser();

        final String encodedPassword = passwordEncoder.encode(password);

        user.setPassword(encodedPassword);

        // Delete Token. It is not needed anymore
        passwordResetTokenDao.deletePasswordResetToken(passwordResetToken);

        LOGGER.info("User {} has updated their password successfully", user.getId());

        return user;
    }

    @Transactional
    @Override
    public void bookmarkPost(User user, Post post) throws IllegalPostBookmarkException {

        if(!post.isEnabled())
            throw new IllegalPostBookmarkException();

        user.bookmarkPost(post);
    }

    @Transactional
    @Override
    public void unbookmarkPost(User user, Post post) throws IllegalPostUnbookmarkException {

        if(!post.isEnabled())
            throw new IllegalPostUnbookmarkException();

        user.unbookmarkPost(post);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasUserBookmarkedPost(User user, Post post) {
        return user.isEnabled() && post.isEnabled() && user.isPostBookmarked(post);
    }

    @Transactional(readOnly = true)
    @Override
    public long getFollowerCount(User user) {
        return userDao.getFollowerCount(user);
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
    public PaginatedCollection<User> getAllUsers(Boolean enabled, String sortCriteria, int pageNumber, int pageSize) {
        return userDao.getAllUsers(enabled, getUserSortCriteria(sortCriteria), pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<User> getFollowedUsers(User user, Boolean enabled, String sortCriteria, int pageNumber, int pageSize) {
        return userDao.getFollowedUsers(user, enabled, getUserSortCriteria(sortCriteria), pageNumber, pageSize);
    }

    @Override
    public UserDao.SortCriteria getUserSortCriteria(String sortCriteriaName) {
        if(sortCriteriaName != null && sortCriteriaMap.containsKey(sortCriteriaName))
            return sortCriteriaMap.get(sortCriteriaName);

        else
            throw new InvalidSortCriteriaException();
    }

    @Override
    public Collection<String> getUserSortOptions() {
        return sortCriteriaMap.keySet();
    }
}
