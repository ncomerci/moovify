package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.models.Movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class MovieDaoImpl implements MovieDao {

    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String MOVIES = TableNames.MOVIES.getTableName();
    private static final String POST_MOVIE = TableNames.POST_MOVIE.getTableName();

    private static final RowMapper<Movie> MOVIE_ROW_MAPPER = (rs, rowNum) ->
            new Movie(rs.getLong("movie_id"), rs.getObject("creation_date", LocalDateTime.class),
                    rs.getString("title"), rs.getObject("premier_date", LocalDate.class));

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public MovieDaoImpl(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);

        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName(MOVIES)
                .usingGeneratedKeyColumns("movie_id");
    }

    @Override
    public Movie register(String title, LocalDate premierDate) {

        // TODO; Require Not Null ????
        Objects.requireNonNull(title);
        Objects.requireNonNull(premierDate);

        LocalDateTime creationDate = LocalDateTime.now();

        HashMap<String, Object> map = new HashMap<>();
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("title", title);
        map.put("premier_date", premierDate);

        final long id = jdbcInsert.executeAndReturnKey(map).longValue();
        return new Movie(id, creationDate, title, premierDate);
    }

    @Override
    public Optional<Movie> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM " + MOVIES + " WHERE " + MOVIES + ".movie_id = ?",
                new Object[]{ id }, MOVIE_ROW_MAPPER).stream().findFirst();
    }

    @Override
    public Collection<Movie> findMoviesByPostId(long postId) {

        return jdbcTemplate.query(
                "SELECT * FROM " + MOVIES +
                        " WHERE " + MOVIES + ".movie_id IN (" +
                        "SELECT " + POST_MOVIE + ".movie_id FROM " + POST_MOVIE + " WHERE " + POST_MOVIE + ".post_id = ?)",
                new Object[]{ postId }, MOVIE_ROW_MAPPER
        );
    }

    @Override
    public Collection<Movie> getAllMovies(){
        return jdbcTemplate.query(
                "SELECT * FROM " + MOVIES, MOVIE_ROW_MAPPER);
    }

    @Override
    public Collection<Movie> searchMovies(String query) {
        return jdbcTemplate.query("SELECT * FROM " + MOVIES + " WHERE " + MOVIES + ".title ILIKE '%' || ? || '%'",
                new Object[]{ query }, MOVIE_ROW_MAPPER);
    }
}
