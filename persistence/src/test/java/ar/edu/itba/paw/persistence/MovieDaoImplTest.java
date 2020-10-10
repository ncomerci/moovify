package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.Post;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class MovieDaoImplTest {
    private static final Map<String, Object> MOVIE_ROW = new HashMap<>();
    private static final Map<String, Object> MOVIE_TO_MOVIE_CATEGORY_ROW = new HashMap<>();
    
    @Autowired
    private MovieDao movieDao;
    
    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert movieJdbcInsert;
    private SimpleJdbcInsert movieToMovieInsert;

    private void mapInitializer() {
        MOVIE_ROW.put("creation_date", Timestamp.valueOf(LocalDateTime.now()));
        MOVIE_ROW.put("release_date", Timestamp.valueOf(LocalDateTime.now()));
        MOVIE_ROW.put("title", "");
        MOVIE_ROW.put("original_title", "");
        MOVIE_ROW.put("tmdb_id", 1);
        MOVIE_ROW.put("imdb_id", "");
        MOVIE_ROW.put("original_language", "");
        MOVIE_ROW.put("overview", "");
        MOVIE_ROW.put("popularity", 5.2);
        MOVIE_ROW.put("runtime", 5.2);
        MOVIE_ROW.put("vote_average", 5.2);

        MOVIE_TO_MOVIE_CATEGORY_ROW.put("tmdb_category_id", 10751);
        MOVIE_TO_MOVIE_CATEGORY_ROW.put("tmdb_id", MOVIE_ROW.get("tmdb_id"));
    }

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.movieJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TableNames.MOVIES.getTableName())
                .usingGeneratedKeyColumns("movie_id");
        this.movieToMovieInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TableNames.MOVIE_TO_MOVIE_CATEGORY.getTableName());
        mapInitializer();
    }

    @Rollback
    @Test
    public void testRegister() {
        //        1. precondiciones
        final String title = "title";
        final String originalTitle = "originalTitle";
        final String originalLanguage = "ES";

        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.MOVIES.getTableName(), "title = ? AND original_title = ? AND original_language = ?", title, originalTitle, originalLanguage);

//        2. ejercitar
        Movie movie =  movieDao.register(title, originalTitle, 12, "", originalLanguage, "", 1.5f, 1.5f, 1.5f, LocalDate.now(), Collections.singleton(12L));

//        3. post-condiciones
        Assert.assertNotNull(movie);
        final String whereClause = String.format("title = '%s' AND original_title = '%s' AND original_language = '%s'", title, originalTitle, originalLanguage);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.MOVIES.getTableName(), whereClause)
        );
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidRegister() {
        movieDao.register(null, null, 0, null, null, null, 0, 0, 0, null, null);
    }

    @Rollback
    @Test
    public void testFindMovieById() {
//        1. precondiciones
        long id = movieJdbcInsert.executeAndReturnKey(MOVIE_ROW).longValue();
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.MOVIE_TO_MOVIE_CATEGORY.getTableName(),
                "tmdb_category_id = ? AND tmdb_id = ?",MOVIE_TO_MOVIE_CATEGORY_ROW.get("tmdb_category_id"),  MOVIE_ROW.get("tmdb_id"));
        movieToMovieInsert.execute(MOVIE_TO_MOVIE_CATEGORY_ROW);

//        2. ejercitar
        final Optional<Movie> movie = movieDao.findMovieById(id);

//        3. post-condiciones
        Assert.assertTrue(movie.isPresent());
        Assert.assertEquals(movie.get().getId(), id);
        final String whereClause = String.format("movie_id = %d AND title = '%s' AND original_title = '%s'", id, MOVIE_ROW.get("title"), MOVIE_ROW.get("original_title"));
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.MOVIES.getTableName(), whereClause)
        );
    }

    @Rollback
    @Test
    public void testGetAllMoviesNotPaginated() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.MOVIES.getTableName());

        long id = movieJdbcInsert.executeAndReturnKey(MOVIE_ROW).longValue();
        movieToMovieInsert.execute(MOVIE_TO_MOVIE_CATEGORY_ROW);

        int[] aux_ids = {2, 3, 4};
        long[] ids = new long[3];

        for(int i = 0; i < aux_ids.length ; i++) {
            MOVIE_ROW.put("tmdb_id", aux_ids[i]);
            MOVIE_ROW.put("imdb_id", String.format("%d", aux_ids[i]));
            ids[i] = movieJdbcInsert.executeAndReturnKey(MOVIE_ROW).longValue();
            MOVIE_TO_MOVIE_CATEGORY_ROW.put("tmdb_id", MOVIE_ROW.get("tmdb_id"));
            movieToMovieInsert.execute(MOVIE_TO_MOVIE_CATEGORY_ROW);
        }

//        2. ejercitar
        final Collection<Movie> moviesNotPaginated = movieDao.getAllMoviesNotPaginated();

//        3. post-condiciones
        Assert.assertNotNull(moviesNotPaginated);
        Assert.assertEquals(4, moviesNotPaginated.size());
        final String whereClause = String.format("movie_id = %d OR movie_id = %d OR movie_id = %d OR movie_id = %d", id, ids[0], ids[1], ids[2]);
        Assert.assertEquals(4,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.MOVIES.getTableName(), whereClause)
        );
    }
}