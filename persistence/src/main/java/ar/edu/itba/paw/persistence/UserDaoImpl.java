package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.RoleDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerificationToken;
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
    private static final String TOKEN = TableNames.USER_VERIFICATION_TOKEN.getTableName();

    private static final String SELECT_FROM_USERS = "SELECT " +
            USERS + ".user_id u_user_id, " +
            USERS + ".creation_date u_creation_date, " +
            USERS + ".enabled u_enabled, " +
            USERS + ".username u_username, " +
            USERS + ".password u_password, " +
            USERS + ".name u_name, " +
            USERS + ".email u_email, " +

            ROLES + ".role_id r_role_id, " +
            ROLES + ".role r_role " +

            "FROM " + USERS +
            " INNER JOIN " + USER_ROLE + " ON " + USERS + ".user_id = " + USER_ROLE + ".user_id " +
            "INNER JOIN " + ROLES + " ON " + USER_ROLE + ".role_id = " + ROLES + ".role_id ";



    private static final ResultSetExtractor<Collection<User>> USER_ROW_MAPPER = (rs) -> {
        Map<Long, User> idToUserMap = new LinkedHashMap<>();
        long userId;

        while(rs.next()){

            userId = rs.getLong("u_user_id");

            if(!idToUserMap.containsKey(userId)) {
                idToUserMap.put(userId,
                        new User(userId, rs.getObject("u_creation_date", LocalDateTime.class),
                        rs.getBoolean("u_enabled"), rs.getString("u_username"),
                        rs.getString("u_password"), rs.getString("u_name"),
                        rs.getString("u_email"), new ArrayList<>())
                );
            }

            idToUserMap.get(userId).getRoles().add(
                    new Role(rs.getLong("r_role_id"), rs.getString("r_role"))
            );
        }

        return idToUserMap.values();
    };

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcUserInsert;
    private final SimpleJdbcInsert jdbcUserRoleInsert;
    private final SimpleJdbcInsert jdbcTokenInsert;

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

        jdbcTokenInsert = new SimpleJdbcInsert(ds)
                .withTableName(TOKEN)
                .usingGeneratedKeyColumns("token_id");
    }

    @Override
    public User register(String username, String password, boolean enabled, String name, String email, Collection<String> roleNames) {

        LocalDateTime creationDate = LocalDateTime.now();

        Collection<Role> roles = roleDao.findRolesByName(roleNames);

        if(roles == null || roles.isEmpty())
            throw new UserRegistrationWithoutRoleException();

        HashMap<String, Object> map = new HashMap<>();
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("enabled", enabled);
        map.put("username", username);
        map.put("password", password);
        map.put("name", name);
        map.put("email", email);

        final long userId = jdbcUserInsert.executeAndReturnKey(map).longValue();

        for(Role role: roles){
            map = new HashMap<>();
            map.put("user_id", userId);
            map.put("role_id", role.getId());
            jdbcUserRoleInsert.execute(map);
        }

        return new User(userId, creationDate, enabled, username, password, name, email, roles);
    }

    @Override
    public long createVerificationToken(String token, LocalDateTime expiryTimestamp, long userId) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("expiry", expiryTimestamp);
        map.put("user_id", userId);

        return jdbcTokenInsert.executeAndReturnKey(map).longValue();
    }

    @Override
    public void enableUser(long userId) {
        // Enable user
        jdbcTemplate.update("UPDATE " + USERS + " SET enabled = TRUE WHERE user_id = ?", userId);

        // Delete row from token table. It is not needed anymore
        jdbcTemplate.update("DELETE FROM " + TOKEN + " WHERE user_id = ?", userId);
    }

    @Override
    public Optional<User> findById(long id) {
        return jdbcTemplate.query(SELECT_FROM_USERS + " WHERE " + USERS + ".user_id = ?",
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
    public Collection<User> getAllUsers() {
        return jdbcTemplate.query(
                SELECT_FROM_USERS, USER_ROW_MAPPER);
    }

    private static final String SELECT_TOKEN_QUERY = "SELECT " +
            USERS + ".user_id u_user_id, " +
            USERS + ".creation_date u_creation_date, " +
            USERS + ".enabled u_enabled, " +
            USERS + ".username u_username, " +
            USERS + ".password u_password, " +
            USERS + ".name u_name, " +
            USERS + ".email u_email, " +

            ROLES + ".role_id r_role_id, " +
            ROLES + ".role r_role, " +

            TOKEN + ".token_id t_token_id, " +
            TOKEN + ".token t_token, " +
            TOKEN + ".expiry t_expiry " +

            "FROM " + TOKEN +
            " INNER JOIN " + USERS + " ON " + TOKEN + ".user_id = " + USERS + ".user_id " +
            "INNER JOIN " + USER_ROLE + " ON " + USERS + ".user_id = " + USER_ROLE + ".user_id " +
            "INNER JOIN " + ROLES + " ON " + USER_ROLE + ".role_id = " + ROLES + ".role_id " +
            "WHERE " + TOKEN + ".token = ?";

    private static final ResultSetExtractor<UserVerificationToken> TOKEN_ROW_MAPPER = (rs) -> {
        if(!rs.next())
            return null;

        final UserVerificationToken token =
                new UserVerificationToken(rs.getLong("t_token_id"), rs.getString("t_token"),
                        rs.getObject("t_expiry", LocalDateTime.class),
                        new User(rs.getLong("u_user_id"), rs.getObject("u_creation_date", LocalDateTime.class),
                                rs.getBoolean("u_enabled"), rs.getString("u_username"),
                                rs.getString("u_password"), rs.getString("u_name"),
                                rs.getString("u_email"), new ArrayList<>()
                        )
                );

        token.getUser().getRoles().add(
                new Role(rs.getLong("r_role_id"), rs.getString("r_role"))
        );

        // All repeated rows may only change because of the user role
        while(rs.next()) {

            token.getUser().getRoles().add(
                    new Role(rs.getLong("r_role_id"), rs.getString("r_role"))
            );
        }

        return token;
    };

    @Override
    public Optional<UserVerificationToken> getVerificationToken(String token) {
        return Optional.ofNullable(jdbcTemplate.query(
                SELECT_TOKEN_QUERY, new Object[]{ token }, TOKEN_ROW_MAPPER));
    }
}
