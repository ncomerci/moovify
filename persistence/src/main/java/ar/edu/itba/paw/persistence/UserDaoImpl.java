package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateEmailException;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUsernameException;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public User register(String username, String password, String name, String email, String description, Collection<String> roleNames, Long avatarId, boolean enabled) throws DuplicateEmailException, DuplicateUsernameException {

        return null;
    }

    @Override
    public void updateName(User user, String name) {

    }

    @Override
    public void updateUsername(User user, String username) throws DuplicateUsernameException {

    }

    @Override
    public void updateDescription(User user, String description) {

    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public void restoreUser(User user) {

    }

    @Override
    public void replaceUserRole(User user, String newRole, String oldRole) {

    }

    @Override
    public int hasUserLiked(User user, Post post) {
        return 0;
    }

    @Override
    public Collection<Role> addRoles(User user, Collection<String> roleNames) {
        return null;
    }

    @Override
    public void updatePassword(User user, String password) {

    }

    @Override
    public void updateAvatarId(User user, long avatarId) {

    }

    @Override
    public Optional<User> findUserById(long id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findDeletedUserById(long id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public PaginatedCollection<User> getAllUsers(SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<User> searchUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<User> searchUsersByRole(String query, String role, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<User> searchDeletedUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }
}
