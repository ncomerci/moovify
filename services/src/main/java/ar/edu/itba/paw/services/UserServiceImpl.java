package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    // All users are created with this role by default
    private static final String defaultUserRole = "ROLE_USER";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(String username, String password, String name, String email) {
        return userDao.register(username, passwordEncoder.encode(password), name, email, Collections.singletonList(defaultUserRole));
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
}
