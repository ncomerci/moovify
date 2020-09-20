package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.User;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {

    User register(String username, String password, String name, String email, Collection<String> roleNames);

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Collection<User> searchUsers(String query);

    Collection<User> getAllUsers();
}
