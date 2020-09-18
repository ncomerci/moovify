package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.User;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {

    User register(String username, String password, String name, String email);

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    Collection<User> getAllUsers();
}
