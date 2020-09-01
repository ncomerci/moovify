package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import java.util.Optional;


@Repository
public class PostDaoImpl implements PostDao {

    private static final String postDb = "posts";

    private static final RowMapper<Post> POST_ROW_MAPPER = (rs, rowNum) ->
            new Post(rs.getLong("id"), rs.getObject("timestamp", LocalDateTime.class),
                    rs.getString("title"), rs.getString("body"),
                    rs.getInt("wordCount"), rs.getString("email"));

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Autowired
    public PostDaoImpl(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(postDb)
                .usingGeneratedKeyColumns("postId");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS posts (" +
                        "postId SERIAL PRIMARY KEY," +
                        "creationDate TIMESTAMP NOT NULL" +
                        "title VARCHAR(50) NOT NULL" +
                        "email VARCHAR(40) NOT NULL" +
                        "wordCount INTEGER " +
                        "body VARCHAR "
                    );
    }

    @Override
    public Optional<Post> findById(long id){
        List<Post> results = jdbcTemplate.query("SELECT * FROM ? WHERE id= ?", new Object[]{ postDb , id }, POST_ROW_MAPPER);

        return results.stream().findFirst();
    }

    @Override
    public Post register(String title, String email, String body) {

        body = body.trim();
        LocalDateTime creationDate = LocalDateTime.now();
        int wordCount = body.split("\\s+").length;

        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("creationDate", Timestamp.valueOf(creationDate));
        map.put("email", email);
        map.put("wordCount", wordCount);
        map.put("body", body);

        final Number key = jdbcInsert.executeAndReturnKey(map);
        return new Post(key.longValue(), creationDate, title, body, wordCount, email );
    }

}
