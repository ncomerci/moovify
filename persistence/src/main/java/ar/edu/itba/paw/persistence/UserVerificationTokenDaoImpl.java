package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserVerificationTokenDao;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

@Repository
public class UserVerificationTokenDaoImpl implements UserVerificationTokenDao {

    private static final String USER_VERIFICATION_TOKEN = TableNames.USER_VERIFICATION_TOKEN.getTableName();
    private static final String USERS = TableNames.USERS.getTableName();
    private static final String ROLES = TableNames.ROLES.getTableName();
    private static final String USER_ROLE = TableNames.USER_ROLE.getTableName();
    private static final String COMMENTS_LIKES = TableNames.COMMENTS_LIKES.getTableName();


    private static final String GET_TOKEN_QUERY = "SELECT " +
            USERS + ".user_id u_user_id, " +
            USERS + ".creation_date u_creation_date, " +
            USERS + ".username u_username, " +
            USERS + ".password u_password, " +
            USERS + ".name u_name, " +
            USERS + ".email u_email, " +
            USERS + ".enabled u_enabled, " +

            ROLES + ".role_id r_role_id, " +
            ROLES + ".role r_role, " +

            USER_VERIFICATION_TOKEN + ".token_id t_token_id, " +
            USER_VERIFICATION_TOKEN + ".token t_token, " +
            USER_VERIFICATION_TOKEN + ".expiry t_expiry, " +

            COMMENTS_LIKES + ".comment_id c_comment_id " +

            "FROM " + USER_VERIFICATION_TOKEN +
            " INNER JOIN " + USERS + " ON " + USER_VERIFICATION_TOKEN + ".user_id = " + USERS + ".user_id " +
            "INNER JOIN " + USER_ROLE + " ON " + USERS + ".user_id = " + USER_ROLE + ".user_id " +
            "INNER JOIN " + ROLES + " ON " + USER_ROLE + ".role_id = " + ROLES + ".role_id " +
            "LEFT OUTER JOIN " + COMMENTS_LIKES + " ON " + COMMENTS_LIKES + ".user_id = " + USERS + ".user_id " +
            "WHERE " + USER_VERIFICATION_TOKEN + ".token = ?";


    private static final ResultSetExtractor<UserVerificationToken> TOKEN_ROW_MAPPER = (rs) -> {
        if(!rs.next())
            return null;

        final UserVerificationToken token =
                new UserVerificationToken(rs.getLong("t_token_id"), rs.getString("t_token"),
                        rs.getObject("t_expiry", LocalDateTime.class),
                        new User(rs.getLong("u_user_id"), rs.getObject("u_creation_date", LocalDateTime.class),
                                rs.getString("u_username"), rs.getString("u_password"),
                                rs.getString("u_name"), rs.getString("u_email"),
                                new HashSet<>(), rs.getBoolean("u_enabled"),  new HashSet<>()
                        )
                );

        token.getUser().getRoles().add(
                new Role(rs.getLong("r_role_id"), rs.getString("r_role"))
        );

        token.getUser().getLikedComments().add(rs.getLong("c_comment_id"));

        // All repeated rows may only change because of the user role
        while(rs.next()) {

            token.getUser().getRoles().add(
                    new Role(rs.getLong("r_role_id"), rs.getString("r_role"))
            );

            token.getUser().getLikedComments().add(rs.getLong("c_comment_id"));
        }

        return token;
    };


    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcTokenInsert;

    @Autowired
    public UserVerificationTokenDaoImpl(final DataSource ds){

        jdbcTemplate = new JdbcTemplate(ds);

        jdbcTokenInsert = new SimpleJdbcInsert(ds)
                .withTableName(USER_VERIFICATION_TOKEN)
                .usingGeneratedKeyColumns("token_id");
    }

    // TODO: Insert or Update instead of Delete and Insert
    @Override
    public long createVerificationToken(String token, LocalDateTime expiryDate, long userId) {

        deleteVerificationToken(userId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("expiry", expiryDate);
        map.put("user_id", userId);

        return jdbcTokenInsert.executeAndReturnKey(map).longValue();
    }

    @Override
    public Optional<UserVerificationToken> getVerificationToken(String token) {
        return Optional.ofNullable(jdbcTemplate.query(
                GET_TOKEN_QUERY, new Object[]{ token }, TOKEN_ROW_MAPPER));
    }

    @Override
    public void deleteVerificationToken(long userId) {
        jdbcTemplate.update("DELETE FROM " + USER_VERIFICATION_TOKEN + " WHERE user_id = ?", userId);
    }
}
