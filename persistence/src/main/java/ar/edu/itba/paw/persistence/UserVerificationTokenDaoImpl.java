package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserVerificationTokenDao;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerificationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(UserVerificationTokenDaoImpl.class);

    private static final String USER_VERIFICATION_TOKEN = TableNames.USER_VERIFICATION_TOKEN.getTableName();
    private static final String USERS = TableNames.USERS.getTableName();
    private static final String ROLES = TableNames.ROLES.getTableName();
    private static final String USER_ROLE = TableNames.USER_ROLE.getTableName();
    private static final String COMMENTS_LIKES = TableNames.COMMENTS_LIKES.getTableName();
    private static final String COMMENTS = TableNames.COMMENTS.getTableName();
    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String POSTS_LIKES = TableNames.POSTS_LIKES.getTableName();


    private static final String GET_TOKEN_QUERY = "SELECT " +
            USERS + ".user_id u_user_id, " +
            USERS + ".creation_date u_creation_date, " +
            USERS + ".username u_username, " +
            USERS + ".password u_password, " +
            USERS + ".name u_name, " +
            USERS + ".email u_email, " +
            USERS + ".description u_description, " +
            USERS + ".avatar_id u_avatar_id, " +
            USERS + ".enabled u_enabled, " +

            ROLES + ".role_id r_role_id, " +
            ROLES + ".role r_role, " +

            "TOTAL_LIKES.total_likes u_total_likes, " +

            USER_VERIFICATION_TOKEN + ".token_id t_token_id, " +
            USER_VERIFICATION_TOKEN + ".token t_token, " +
            USER_VERIFICATION_TOKEN + ".expiry t_expiry, " +

            COMMENTS_LIKES + ".comment_id c_comment_id " +

            "FROM " + USER_VERIFICATION_TOKEN +

            " INNER JOIN " + USERS + " ON " + USER_VERIFICATION_TOKEN + ".user_id = " + USERS + ".user_id " +

            " INNER JOIN ( " +
                "SELECT " + USERS + ".user_id, coalesce(post_likes.total_likes, 0) + coalesce(comment_likes.total_likes, 0) total_likes " +
                "FROM " + USERS +
                    " LEFT OUTER JOIN ( " +
                        "SELECT " + POSTS + ".user_id, SUM(" + POSTS_LIKES + ".value) total_likes " +
                        "FROM " + POSTS +
                            " INNER JOIN " + POSTS_LIKES + " ON " + POSTS + ".post_id = " + POSTS_LIKES + ".post_id " +
                        "GROUP BY " + POSTS + ".user_id " +
                    ") post_likes ON " + USERS + ".user_id = post_likes.user_id " +

                    "LEFT OUTER JOIN ( " +
                        "SELECT " + COMMENTS + ".user_id, SUM(" + COMMENTS_LIKES + ".value) total_likes " +
                        "FROM " + COMMENTS +
                            " INNER JOIN " + COMMENTS_LIKES + " ON " + COMMENTS + ".comment_id = " + COMMENTS_LIKES + ".comment_id " +
                        "GROUP BY " + COMMENTS + ".user_id " +
                    ") comment_likes ON " + USERS + ".user_id = comment_likes.user_id " +
            ") TOTAL_LIKES ON TOTAL_LIKES.user_id = " + USERS + ".user_id " +

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
                                rs.getString("u_description"),
                                rs.getLong("u_avatar_id"), rs.getLong("u_total_likes"),
                                new HashSet<>(), rs.getBoolean("u_enabled")
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
    public UserVerificationToken createVerificationToken(String token, LocalDateTime expiryDate, User user) {

        deleteVerificationToken(user);

        HashMap<String, Object> map = new HashMap<>();

        map.put("token", token);
        map.put("expiry", expiryDate);
        map.put("user_id", user.getId());

        final long tokenId = jdbcTokenInsert.executeAndReturnKey(map).longValue();

        final UserVerificationToken userVerificationToken = new UserVerificationToken(tokenId, token, expiryDate, user);

        LOGGER.info("Created UserVerificationToken {}", userVerificationToken.getId());
        LOGGER.debug("Created UserVerificationToken {}", userVerificationToken);

        return userVerificationToken;
    }

    @Override
    public Optional<UserVerificationToken> getVerificationToken(String token) {

        LOGGER.info("Get User Verficiation Token {}", token);
        return Optional.ofNullable(jdbcTemplate.query(
                GET_TOKEN_QUERY, new Object[]{ token }, TOKEN_ROW_MAPPER));
    }

    @Override
    public void deleteVerificationToken(User user) {

        jdbcTemplate.update("DELETE FROM " + USER_VERIFICATION_TOKEN + " WHERE user_id = ?", user.getId());

        LOGGER.info("Deleted Verification Token from User {}", user.getId());
    }
}
