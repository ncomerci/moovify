package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateEmailException;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUsernameException;
import ar.edu.itba.paw.models.*;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {

    enum SortCriteria {
        NEWEST, OLDEST, LIKES, USERNAME
    }

    User register(String username, String password, String name, String email, String description, Collection<Role> roleNames, Image avatar, boolean enabled) throws DuplicateEmailException, DuplicateUsernameException;

    Optional<User> findUserById(long id);

    Optional<User> findDeletedUserById(long id);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    PaginatedCollection<User> getAllUsers(SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> searchUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> searchUsersByRole(String query, String role, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> searchDeletedUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize);
}
