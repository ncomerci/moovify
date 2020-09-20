import Config.TestConfig;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.persistence.MovieDaoImpl;
import ar.edu.itba.paw.persistence.TableNames;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class MovieDaoImplTest {

    private static final String MOVIE_TITLE = "MovieTest";
    private static final String MOVIE_TITLE_COM = "'" + MOVIE_TITLE + "'";
    private static final LocalDate RELEASE_DATE = LocalDate.now();

    @Autowired
    private MovieDaoImpl movieDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TableNames.MOVIES.getTableName())
                .usingGeneratedKeyColumns("movie_id");
    }



    @Test
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testRegister() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.MOVIES.getTableName(), "title = ?", MOVIE_TITLE);

//        2. ejercitar
        Movie movie = movieDao.register(MOVIE_TITLE, RELEASE_DATE);

//        3. post-condiciones
        Assert.assertNotNull(movie);
        Assert.assertEquals(MOVIE_TITLE, movie.getTitle());
        Assert.assertEquals(RELEASE_DATE, movie.getPremierDate());
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.MOVIES.getTableName(), "title = " + MOVIE_TITLE_COM)
        );

    }

    @Test(expected = NullPointerException.class)
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testInvalidRegister() {
        movieDao.register(null, null);
    }

    @Test(expected = DuplicateKeyException.class)
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testRegisterAlreadyExists() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.MOVIES.getTableName(), "title = ?", MOVIE_TITLE);
        HashMap<String, Object> map = new HashMap<>();
        map.put("creation_date", Timestamp.valueOf(LocalDateTime.now()));
        map.put("title", MOVIE_TITLE);
        map.put("premier_date", RELEASE_DATE);
        jdbcInsert.execute(map);

//        2. ejercitar
        movieDao.register(MOVIE_TITLE, RELEASE_DATE);

//        3. post-condiciones
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.MOVIES.getTableName(), "title = " + MOVIE_TITLE_COM)
        );

    }

    @Test
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testValidFindById() {
//        1. precondiciones
        HashMap<String, Object> map = new HashMap<>();
        map.put("creation_date", Timestamp.valueOf(LocalDateTime.now()));
        map.put("title", "MOVIE_TITLE");
        map.put("premier_date", RELEASE_DATE);
        Number key = jdbcInsert.executeAndReturnKey(map);

//        2. ejercitar
        Optional<Movie> movie = movieDao.findById(key.longValue());

//        3. post-condiciones
        Assert.assertTrue(movie.isPresent());
        Assert.assertEquals("MOVIE_TITLE", movie.get().getTitle());
    }

    @Test
    @Sql("classpath:test_inserts.sql")
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testFindMoviesByPostId() {
//        1. precondiciones

//        2. ejercitar
        ArrayList<Movie> movies = (ArrayList<Movie>) movieDao.findMoviesByPostId(1);

//        3. post-condiciones
        Assert.assertNotNull(movies);
        Assert.assertEquals(JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POST_MOVIE.getTableName(), "post_id = 1"),
                movies.size());
    }

    @Test
    @Sql("classpath:test_inserts.sql")
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetAllMovies() {
//        1. precondiciones

//        2. ejercitar
        Collection<Movie> movies = movieDao.getAllMovies();

//        3. post-condiciones
        Assert.assertNotNull(movies);
        Assert.assertEquals(JdbcTestUtils.countRowsInTable(jdbcTemplate, TableNames.MOVIES.getTableName()), movies.size());
    }
}
