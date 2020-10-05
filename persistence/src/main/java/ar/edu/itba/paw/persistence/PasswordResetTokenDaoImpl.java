package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PasswordResetTokenDao;
import ar.edu.itba.paw.models.PasswordResetToken;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

@Repository
public class PasswordResetTokenDaoImpl implements PasswordResetTokenDao {

    private static final String PASSWORD_RESET_TOKEN = TableNames.PASSWORD_RESET_TOKEN.getTableName();
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
            USERS + ".avatar_id u_avatar_id, " +
            USERS + ".enabled u_enabled, " +

            ROLES + ".role_id r_role_id, " +
            ROLES + ".role r_role, " +

            PASSWORD_RESET_TOKEN + ".token_id t_token_id, " +
            PASSWORD_RESET_TOKEN + ".token t_token, " +
            PASSWORD_RESET_TOKEN + ".expiry t_expiry, " +

            COMMENTS_LIKES + ".comment_id c_comment_id " +

            "FROM " + PASSWORD_RESET_TOKEN +
            " INNER JOIN " + USERS + " ON " + PASSWORD_RESET_TOKEN + ".user_id = " + USERS + ".user_id " +
            "INNER JOIN " + USER_ROLE + " ON " + USERS + ".user_id = " + USER_ROLE + ".user_id " +
            "INNER JOIN " + ROLES + " ON " + USER_ROLE + ".role_id = " + ROLES + ".role_id " +
            "LEFT OUTER JOIN " + COMMENTS_LIKES + " ON " + COMMENTS_LIKES + ".user_id = " + USERS + ".user_id "+
            "WHERE " + PASSWORD_RESET_TOKEN + ".token = ?";


    private static final ResultSetExtractor<PasswordResetToken> TOKEN_ROW_MAPPER = (rs) -> {
        if(!rs.next())
            return null;

        final PasswordResetToken token =
                new PasswordResetToken(rs.getLong("t_token_id"), rs.getString("t_token"),
                        rs.getObject("t_expiry", LocalDateTime.class),
                        new User(rs.getLong("u_user_id"), rs.getObject("u_creation_date", LocalDateTime.class),
                                rs.getString("u_username"), rs.getString("u_password"),
                                rs.getString("u_name"), rs.getString("u_email"),
                                rs.getString("u_description"),
                                rs.getLong("u_avatar_id"),
                                new ArrayList<>(), rs.getBoolean("u_enabled"), new HashSet<>()
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
        }

        return token;
    };

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcTokenInsert;

    @Autowired
    public PasswordResetTokenDaoImpl(final DataSource ds){

        jdbcTemplate = new JdbcTemplate(ds);

        jdbcTokenInsert = new SimpleJdbcInsert(ds)
                .withTableName(PASSWORD_RESET_TOKEN)
                .usingGeneratedKeyColumns("token_id");
    }

    @Override
    public long createPasswordResetToken(String token, LocalDateTime expiryDate, long userId) {

        deletePasswordResetToken(userId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("expiry", expiryDate);
        map.put("user_id", userId);

        return jdbcTokenInsert.executeAndReturnKey(map).longValue();
    }

    @Override
    public Optional<PasswordResetToken> getResetPasswordToken(String token) {
        return Optional.ofNullable(jdbcTemplate.query(
                GET_TOKEN_QUERY, new Object[]{ token }, TOKEN_ROW_MAPPER));
    }

    @Override
    public void deletePasswordResetToken(long userId) {
        jdbcTemplate.update("DELETE FROM " + PASSWORD_RESET_TOKEN + " WHERE user_id = ?", userId);
    }
}
