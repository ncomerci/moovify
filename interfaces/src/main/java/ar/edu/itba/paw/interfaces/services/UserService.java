package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;

public interface UserService {

    User register(String username, String password, String name, String email, String description, byte[] avatar, String confirmationMailTemplate);

    void editName(long user_id, String name);

    void editUsername(long user_id, String username);

    void editDescription(long user_id, String description);

    void changePassword(long user_id, String password);

    void addRoles(long userId, Collection<String> roleNames);

    Optional<User> confirmRegistration(String token);

    void createConfirmationEmail(User user, String confirmationMailTemplate);

    void createPasswordResetEmail(User user, String passwordResetMailTemplate);

    boolean validatePasswordResetToken(String token);

    int hasUserLiked(long user_id, long post_id);

    Optional<User> updatePassword(String password, String token);

    Optional<byte[]> getAvatar(long avatarId) throws IOException, URISyntaxException;

    void updateAvatar(User user, byte[] newAvatar);

    boolean emailExistsAndIsValidated(String email);

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    PaginatedCollection<User> getAllUsers(int pageNumber, int pageSize);
}
