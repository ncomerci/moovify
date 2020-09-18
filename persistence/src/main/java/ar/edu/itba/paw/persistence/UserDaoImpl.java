package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    private static final String USERS = TableNames.USERS.getTableName();

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) ->
            new User(rs.getLong("user_id"), rs.getObject("creation_date", LocalDateTime.class),
                    rs.getString("username"), rs.getString("password"),
                    rs.getString("name"), rs.getString("email"));

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public UserDaoImpl(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);

        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(USERS)
                .usingGeneratedKeyColumns("user_id");
    }

    @Override
    public User register(String username, String password, String name, String email) {

        LocalDateTime creationDate = LocalDateTime.now();

        HashMap<String, Object> map = new HashMap<>();
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("username", username);
        map.put("password", password);
        map.put("name", name);
        map.put("email", email);

        final long userId = jdbcInsert.executeAndReturnKey(map).longValue();
        return new User(userId, creationDate, username, password, name, email);
    }

    @Override
    public Optional<User> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM " + USERS + " WHERE " + USERS + ".user_id = ?",
                new Object[]{ id }, USER_ROW_MAPPER).stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jdbcTemplate.query("SELECT * FROM " + USERS + " WHERE " + USERS + ".username = ?",
                new Object[]{ username }, USER_ROW_MAPPER).stream().findFirst();
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbcTemplate.query(
                "SELECT * FROM " + USERS, USER_ROW_MAPPER);
    }
}
