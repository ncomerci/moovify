package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    User register(String username, String password, String name, String email);

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Collection<User> getAllUsers();
}
