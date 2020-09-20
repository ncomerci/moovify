package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    // All users are created with this role by default
    private static final String DEFAULT_USER_ROLE = "ROLE_USER";

    private static final boolean DEFAULT_ENABLED = false;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(String username, String password, String name, String email) {
        return userDao.register(username, passwordEncoder.encode(password), DEFAULT_ENABLED, name, email, Collections.singletonList(DEFAULT_USER_ROLE));
    }

    @Override
    public String createVerificationToken(long userId) {

        final String token = UUID.randomUUID().toString();

        userDao.createVerificationToken(token, UserVerificationToken.calculateExpiryDate(), userId);

        return token;
    }

    @Override
    public Optional<User> confirmRegistration(String token) {
        Optional<UserVerificationToken> optToken = userDao.getVerificationToken(token);

        if(!optToken.isPresent())
            return Optional.empty();

        final UserVerificationToken verificationToken = optToken.get();

        User user = verificationToken.getUser();

        if(verificationToken.isValid()) {
            userDao.enableUser(user.getId());

            user = new User(user.getId(), user.getCreationDate(), true,
                    user.getUsername(), user.getPassword(), user.getName(),
                    user.getEmail(), user.getRoles());
        }

        return Optional.of(user);
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
    public Collection<User> getAllUsers() {
        return userDao.getAllUsers();
    }
}
