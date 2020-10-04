package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.RoleDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.exceptions.UserRegistrationWithoutRoleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {

    private static final String USERS = TableNames.USERS.getTableName();
    private static final String ROLES = TableNames.ROLES.getTableName();
    private static final String USER_ROLE = TableNames.USER_ROLE.getTableName();
    private static final String POSTS_LIKES = TableNames.POSTS_LIKES.getTableName();
    private static final String COMMENTS_LIKES = TableNames.COMMENTS_LIKES.getTableName();

    private static final String SELECT_FROM_USERS = "SELECT " +
            USERS + ".user_id u_user_id, " +
            USERS + ".creation_date u_creation_date, " +
            USERS + ".username u_username, " +
            USERS + ".password u_password, " +
            USERS + ".name u_name, " +
            USERS + ".email u_email, " +
            USERS + ".enabled u_enabled, " +

            ROLES + ".role_id r_role_id, " +
            ROLES + ".role r_role, " +

            COMMENTS_LIKES + ".comment_id c_comment_id " +

            "FROM " + USERS +
            " INNER JOIN " + USER_ROLE + " ON " + USERS + ".user_id = " + USER_ROLE + ".user_id " +
            "INNER JOIN " + ROLES + " ON " + USER_ROLE + ".role_id = " + ROLES + ".role_id " +
            "LEFT OUTER JOIN " + COMMENTS_LIKES + " ON " + COMMENTS_LIKES + ".user_id = " + USERS + ".user_id ";


    private static final ResultSetExtractor<Collection<User>> USER_ROW_MAPPER = (rs) -> {
        Map<Long, User> idToUserMap = new LinkedHashMap<>();
        long userId;

        while(rs.next()){

            userId = rs.getLong("u_user_id");

            if(!idToUserMap.containsKey(userId)) {
                idToUserMap.put(userId,
                        new User(userId, rs.getObject("u_creation_date", LocalDateTime.class),
                                rs.getString("u_username"), rs.getString("u_password"),
                                rs.getString("u_name"), rs.getString("u_email"),
                                new ArrayList<>(), rs.getBoolean("u_enabled"), new HashSet<>())
                );
            }

            idToUserMap.get(userId).getRoles().add(
                    new Role(rs.getLong("r_role_id"), rs.getString("r_role"))
            );
            idToUserMap.get(userId).getLikedComments().add(rs.getLong("c_comment_id"));
        }

        return idToUserMap.values();
    };

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcUserInsert;
    private final SimpleJdbcInsert jdbcUserRoleInsert;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    public UserDaoImpl(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);

        jdbcUserInsert = new SimpleJdbcInsert(ds)
                .withTableName(USERS)
                .usingGeneratedKeyColumns("user_id");

        jdbcUserRoleInsert = new SimpleJdbcInsert(ds)
                .withTableName(USER_ROLE);
    }

    @Override
    public User register(String username, String password, String name, String email, Collection<String> roleNames, boolean enabled) {

        LocalDateTime creationDate = LocalDateTime.now();

        // TODO: Se puede evitar esta query. Importante! Requiere un insert mas complejo.
        Collection<Role> roles = roleDao.findRolesByName(roleNames);

        if(roles == null || roles.isEmpty())
            throw new UserRegistrationWithoutRoleException();

        HashMap<String, Object> map = new HashMap<>();
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("username", username);
        map.put("password", password);
        map.put("name", name);
        map.put("email", email);
        map.put("enabled", enabled);

        final long userId = jdbcUserInsert.executeAndReturnKey(map).longValue();

        for(Role role: roles){
            map = new HashMap<>();
            map.put("user_id", userId);
            map.put("role_id", role.getId());
            jdbcUserRoleInsert.execute(map);
        }

        return new User(userId, creationDate, username, password, name, email, roles, true , Collections.emptySet());
    }

    @Override
    public void updatePassword(long userId, String password) {
        jdbcTemplate.update("UPDATE " + USERS + " SET password = ? WHERE user_id = ?", password, userId);
    }

    @Override
    public Collection<Role> addRoles(long userId, Collection<String> roleNames) {
        Collection<Role> roles = roleDao.findRolesByName(roleNames);

        HashMap<String, Object> map;

        for (Role role: roles) {
            map = new HashMap<>();
            map.put("user_id", userId);
            map.put("role_id", role.getId());
            jdbcUserRoleInsert.execute(map);
        }

        return roles;
    }

    @Override
    public boolean userHasRole(long userId, String role) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + USER_ROLE +
                        " INNER JOIN " + ROLES + " ON " + ROLES + ".role_id = " + USER_ROLE + ".role_id" +
                        " WHERE user_id = ? AND role = ?", new Object[]{ userId, role}, Integer.class) > 0;
    }

    @Override
    public boolean userHasRole(String email, String role) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + USERS +
                        " INNER JOIN "+ USER_ROLE + " ON " + USERS + ".user_id = " + USER_ROLE + ".user_id" +
                        " INNER JOIN " + ROLES + " ON " + ROLES + ".role_id = " + USER_ROLE + ".role_id" +
                        " WHERE email = ? AND role = ?", new Object[]{ email, role }, Integer.class) > 0;
    }

    @Override
    public boolean hasUserLiked(String username, long postId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + USERS +
                " LEFT OUTER JOIN " + POSTS_LIKES + " ON " + USERS + ".user_id = " + POSTS_LIKES + ".user_id" +
                " WHERE username = ? AND post_id = ?", new Object[] { username, postId }, Integer.class) > 0;
    }

    @Override
    public void replaceUserRole(final long userId, final String newRole, final String oldRole) {

        jdbcTemplate.update("UPDATE " + USER_ROLE +
                " SET role_id = (SELECT role_id FROM " + ROLES + " WHERE role = ?)" +
                "FROM " + ROLES +
                " WHERE " + USER_ROLE + ".user_id = ?" +
                "  AND " + ROLES + ".role_id = " + USER_ROLE + ".role_id" +
                "  AND " + ROLES + ".role = ?", newRole, userId, oldRole);
    }

    @Override
    public Optional<User> findById(long id) {
        return jdbcTemplate.query(SELECT_FROM_USERS + " WHERE " + USERS + ".user_id = ?" + " AND "+ USERS + ".enabled = true",
                new Object[]{ id }, USER_ROW_MAPPER).stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jdbcTemplate.query(SELECT_FROM_USERS + " WHERE " + USERS + ".username = ?",
                new Object[]{ username }, USER_ROW_MAPPER).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jdbcTemplate.query(SELECT_FROM_USERS + " WHERE " + USERS + ".email = ?",
                new Object[]{ email }, USER_ROW_MAPPER).stream().findFirst();
    }

    @Override
    public Collection<User> searchUsers(String query) {
        return jdbcTemplate.query(SELECT_FROM_USERS + " WHERE " + USERS + ".username ILIKE '%' || ? || '%'" + " AND "+ USERS + ".enabled = true", new Object[]{query}, USER_ROW_MAPPER);
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbcTemplate.query(
                SELECT_FROM_USERS, USER_ROW_MAPPER);
    }
}
