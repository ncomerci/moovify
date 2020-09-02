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

    private static final String postTableName = "posts";

    private static final RowMapper<Post> POST_ROW_MAPPER = (rs, rowNum) ->
            new Post(rs.getLong("post_id"), rs.getObject("creation_date", LocalDateTime.class),
                    rs.getString("title"), rs.getString("body"),
                    rs.getInt("word_count"), rs.getString("email"));

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public PostDaoImpl(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(postTableName)
                .usingGeneratedKeyColumns("post_id");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + postTableName + " (" +
                        "post_id SERIAL PRIMARY KEY," +
                        "creation_date TIMESTAMP NOT NULL," +
                        "title VARCHAR(50) NOT NULL," +
                        "email VARCHAR(40) NOT NULL," +
                        "word_count INTEGER," +
                        "body VARCHAR )"
                    );
    }

    @Override
    public Optional<Post> findById(long id){
        //TODO: preguntar si es una buena forma de generalizar el nombre de la tabla
        List<Post> results = jdbcTemplate.query("SELECT * FROM " + postTableName + " WHERE post_id= ?", new Object[]{ id }, POST_ROW_MAPPER);

        return results.stream().findFirst();
    }

    @Override
    public Post register(String title, String email, String body) {

        body = body.trim();
        LocalDateTime creationDate = LocalDateTime.now();
        int wordCount = body.split("\\s+").length;

        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("email", email);
        map.put("word_count", wordCount);
        map.put("body", body);

        final Number key = jdbcInsert.executeAndReturnKey(map);
        return new Post(key.longValue(), creationDate, title, body, wordCount, email );
    }

}
