package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import ar.edu.itba.paw.interfaces.services.exceptions.*;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

public interface UserService {

    User register(String username, String password, String name, String email, String description, String confirmationMailTemplate, Locale locale) throws DuplicateUniqueUserAttributeException;

    void updateUser(User user, String name, String username, String description, String password) throws DuplicateUniqueUserAttributeException;

    void updateName(User user, String name);

    void updateUsername(User user, String username) throws DuplicateUniqueUserAttributeException;

    void updateDescription(User user, String description);

    void updatePassword(User user, String password);

    void deleteUser(User user) throws DeletedDisabledModelException;

    void restoreUser(User user) throws RestoredEnabledModelException;

    void promoteUserToAdmin(User user) throws InvalidUserPromotionException;

    void followUser(User user, User userFollowed) throws IllegalUserFollowException;

    void unfollowUser(User user, User userUnfollowed) throws IllegalUserUnfollowException;

    Optional<User> confirmRegistration(String token);

    void createConfirmationEmail(User user, String confirmationMailTemplate, Locale locale);

    void createPasswordResetEmail(User user, String passwordResetMailTemplate, Locale locale);

    boolean validatePasswordResetToken(String token);

    Optional<User> updatePassword(String password, String token);

    Optional<byte[]> getAvatar(User user);

    void updateAvatar(User user, byte[] newAvatar);

    void bookmarkPost(User user, Post post) throws IllegalPostBookmarkException;

    void unbookmarkPost(User user, Post post) throws IllegalPostUnbookmarkException;

    long getFollowerCount(User user);

    Optional<User> findUserById(long id);

    Optional<User> findDeletedUserById(long id);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    PaginatedCollection<User> getAllUsers(String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> getFollowedUsers(User user, String sortCriteria, int pageNumber, int pageSize);

    UserDao.SortCriteria getUserSortCriteria(String sortCriteriaName);

    Collection<String> getUserSortOptions();
}
