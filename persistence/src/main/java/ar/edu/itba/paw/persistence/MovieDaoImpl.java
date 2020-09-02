package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.models.Movie;

import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


public class MovieDaoImpl implements MovieDao {
    private static final String movieTableName = "movies";

    private static final RowMapper<Movie> MOVIE_ROW_MAPPER = (rs, rowNum) ->
            new Movie(rs.getLong("movie_id"), rs.getString("title"),
                    rs.getObject("creation_date", LocalDate.class));

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public MovieDaoImpl(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(movieTableName)
                .usingGeneratedKeyColumns("movie_id");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + movieTableName + " (" +
                "movie_id SERIAL PRIMARY KEY," +
                "premier_date DATE NOT NULL," +
                "title VARCHAR(50) NOT NULL )" );
    }


    @Override
    public Optional<Movie> findById(long id) {
        List<Movie> results = jdbcTemplate.query("SELECT * FROM " + movieTableName + " WHERE movie_id= ?", new Object[]{ id }, MOVIE_ROW_MAPPER);

        return results.stream().findFirst();
    }

    @Override
    public Movie register(String title, LocalDate premierDate) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("premier_date", premierDate);

        final Number key = jdbcInsert.executeAndReturnKey(map);
        return new Movie(key.longValue(), title, premierDate);
    }
}
