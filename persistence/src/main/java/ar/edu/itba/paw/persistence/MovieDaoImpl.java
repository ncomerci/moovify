package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.models.Movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.time.LocalDate;
import java.util.*;

@Repository
public class MovieDaoImpl implements MovieDao {


    private static final RowMapper<Movie> MOVIE_ROW_MAPPER = (rs, rowNum) ->
            new Movie(rs.getLong("movie_id"), rs.getString("title"),
                    rs.getObject("premier_date", LocalDate.class));

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public MovieDaoImpl(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(TableNames.MOVIES.getTableName())
                .usingGeneratedKeyColumns("movie_id");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + TableNames.MOVIES.getTableName() + " (" +
                "movie_id SERIAL PRIMARY KEY," +
                "premier_date DATE NOT NULL," +
                "title VARCHAR(50) NOT NULL )" );
    }


    @Override
    public Optional<Movie> findById(long id) {
        List<Movie> results = jdbcTemplate.query("SELECT * FROM " + TableNames.MOVIES.getTableName() + " WHERE movie_id= ?", new Object[]{ id }, MOVIE_ROW_MAPPER);

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

    @Override
    public Set<Movie> getMoviesByPost(long postId) {

        return new HashSet<>(jdbcTemplate.query(
                "SELECT * FROM " + TableNames.MOVIES.getTableName() +
                        " WHERE movie_id IN (" +
                        "SELECT movie_id FROM " + TableNames.POST_MOVIES.getTableName() + " WHERE post_id = ?)",
                new Object[]{ postId }, MOVIE_ROW_MAPPER)
        );
    }
}
