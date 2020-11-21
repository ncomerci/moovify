package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.MovieCategory;
import ar.edu.itba.paw.models.PaginatedCollection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class MovieDaoImplTest {

    private static final MovieDao.SortCriteria NEWEST = MovieDao.SortCriteria.NEWEST;

    private static final long ACTION_ID = 28L;
    private static final long MOVIE_CATEGORY1_ID = 1L;
    private static final String ACTION_NAME = "action";

    @Autowired
    private MovieDao movieDao;
    
    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;
    private InsertHelper helper;
    
    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);

        this.helper = new InsertHelper(jdbcTemplate);
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testRegister() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        MovieCategory mc = em.find(MovieCategory.class, MOVIE_CATEGORY1_ID);

        // Exercise
        Movie movie =  movieDao.register("title", "originalTitle", 12L, "imdbID",
                "en", "overview", 1.5f, 1.5f, 1.5f,
                LocalDate.of(1998, 8, 6), Collections.singleton(mc));

        em.flush();

        // Post conditions
        Assert.assertNotNull(movie);
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Movie.TABLE_NAME));
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testFindMovieById() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        long movieID = helper.insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(123, 12);

        // Exercise
        final Optional<Movie> movie = movieDao.findMovieById(movieID);

        // Post conditions
        Assert.assertTrue(movie.isPresent());
        Assert.assertEquals(movie.get().getId(), movieID);
    }

    @Test
    public void testFindMovieByNonExistingId() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        long movieID = helper.insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(123, 12);

        // Exercise
        final Optional<Movie> movie = movieDao.findMovieById(movieID + 5);

        // Post conditions
        Assert.assertFalse(movie.isPresent());
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testFindMoviesById() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        long movie1ID = helper.insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(123, 12);
        long movie2ID = helper.insertMovie("TITULO",124,"imdb124", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(124, 12);
        long movie3ID = helper.insertMovie("TITULO",125,"imdb125", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(125, 12);
        long movie4ID = helper.insertMovie("TITULO",126,"imdb126", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(126, 12);

        // Exercise
        final Collection<Movie> movies = movieDao.findMoviesById(Arrays.asList(movie1ID, movie3ID, movie4ID));

        final Collection<Long> movieIds = movies.stream().map(Movie::getId).collect(Collectors.toList());

        // Post conditions
        Assert.assertTrue(Arrays.asList(movie1ID, movie3ID, movie4ID).containsAll(movieIds));
        Assert.assertTrue(movieIds.containsAll(Arrays.asList(movie1ID, movie3ID, movie4ID)));
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testGetAllMoviesNewest() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        long movie1ID = helper.insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(123, 12);
        long movie2ID = helper.insertMovie("MOVIE",124,"imdb124", LocalDate.of(2002,8,6));
        helper.insertMovieToMovieCategory(124, 12);
        long movie3ID = helper.insertMovie("PELICULA",125,"imdb125", LocalDate.of(2003,8,6));
        helper.insertMovieToMovieCategory(125, 12);
        long movie4ID = helper.insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(2005,8,6));
        helper.insertMovieToMovieCategory(126, 12);

        // Exercise
        final PaginatedCollection<Movie> movies = movieDao.getAllMovies(MovieDao.SortCriteria.NEWEST, 1, 2);

        // Post conditions
        Assert.assertEquals(4, movies.getTotalCount());
        Assert.assertArrayEquals(new Long[]{movie2ID, movie1ID}, movies.getResults().stream().map(Movie::getId).toArray());
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testGetAllMoviesOldest() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        long movie1ID = helper.insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(123, 12);
        long movie2ID = helper.insertMovie("MOVIE",124,"imdb124", LocalDate.of(2002,8,6));
        helper.insertMovieToMovieCategory(124, 12);
        long movie3ID = helper.insertMovie("PELICULA",125,"imdb125", LocalDate.of(2003,8,6));
        helper.insertMovieToMovieCategory(125, 12);
        long movie4ID = helper.insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(2005,8,6));
        helper.insertMovieToMovieCategory(126, 12);

        // Exercise
        final PaginatedCollection<Movie> movies = movieDao.getAllMovies(MovieDao.SortCriteria.OLDEST, 1, 2);

        // Post conditions
        Assert.assertEquals(4, movies.getTotalCount());
        Assert.assertArrayEquals(new Long[]{movie3ID, movie4ID}, movies.getResults().stream().map(Movie::getId).toArray());
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testGetAllMoviesTitle() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        long movie1ID = helper.insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(123, 12);
        long movie2ID = helper.insertMovie("MOVIE",124,"imdb124", LocalDate.of(2002,8,6));
        helper.insertMovieToMovieCategory(124, 12);
        long movie3ID = helper.insertMovie("ACE VENTURA",125,"imdb125", LocalDate.of(2003,8,6));
        helper.insertMovieToMovieCategory(125, 12);
        long movie4ID = helper.insertMovie("ZORG",126,"imdb126", LocalDate.of(2005,8,6));
        helper.insertMovieToMovieCategory(126, 12);

        // Exercise
        final PaginatedCollection<Movie> movies = movieDao.getAllMovies(MovieDao.SortCriteria.TITLE, 1, 2);

        // Post conditions
        Assert.assertEquals(4, movies.getTotalCount());
        Assert.assertArrayEquals(new Long[]{movie1ID, movie4ID}, movies.getResults().stream().map(Movie::getId).toArray());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:post1.sql")
    @Sql("classpath:post2.sql")
    @Sql("classpath:post3.sql")
    @Sql("classpath:movie-categories.sql")
    public void testGetAllMoviesPostCount() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        long movieId1 = helper.insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(123, 12);
        helper.insertPostMovie(1, movieId1);
        helper.insertPostMovie(2, movieId1);

        long movieId2 = helper.insertMovie("MOVIE",124,"imdb124", LocalDate.of(2002,8,6));
        helper.insertMovieToMovieCategory(124, 12);
        helper.insertPostMovie(1, movieId2);

        long movieId3 = helper.insertMovie("PELICULA",125,"imdb125", LocalDate.of(2003,8,6));
        helper.insertMovieToMovieCategory(125, 12);

        long movieId4 = helper.insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(2005,8,6));
        helper.insertMovieToMovieCategory(126, 12);
        helper.insertPostMovie(1, movieId4);
        helper.insertPostMovie(2, movieId4);
        helper.insertPostMovie(3, movieId4);

        // Exercise
        final PaginatedCollection<Movie> movies = movieDao.getAllMovies(MovieDao.SortCriteria.POST_COUNT, 1, 2);

        // Post conditions
        Assert.assertEquals(4, movies.getTotalCount());
        Assert.assertArrayEquals(new Long[]{movieId2, movieId3}, movies.getResults().stream().map(Movie::getId).toArray());
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testGetAllMoviesEmptyPage() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        helper.insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(123, 12);
        helper.insertMovie("MOVIE",124,"imdb124", LocalDate.of(2002,8,6));
        helper.insertMovieToMovieCategory(124, 12);
        helper.insertMovie("PELICULA",125,"imdb125", LocalDate.of(2003,8,6));
        helper.insertMovieToMovieCategory(125, 12);
        helper.insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(2005,8,6));
        helper.insertMovieToMovieCategory(126, 12);

        // Exercise
        final PaginatedCollection<Movie> movies = movieDao.getAllMovies(MovieDao.SortCriteria.NEWEST, 10, 10);

        // Post conditions
        Assert.assertEquals(4, movies.getTotalCount());
        Assert.assertEquals(0, movies.getResults().size());
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testGetAllMoviesNotPaginated() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        long movie1ID = helper.insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(123, 12);
        long movie2ID = helper.insertMovie("MOVIE",124,"imdb124", LocalDate.of(2002,8,6));
        helper.insertMovieToMovieCategory(124, 12);
        long movie3ID = helper.insertMovie("ACE VENTURA",125,"imdb125", LocalDate.of(2003,8,6));
        helper.insertMovieToMovieCategory(125, 12);
        long movie4ID = helper.insertMovie("ZORG",126,"imdb126", LocalDate.of(2005,8,6));
        helper.insertMovieToMovieCategory(126, 12);

        // Exercise
        final Collection<Movie> moviesNotPaginated = movieDao.getAllMoviesNotPaginated();

        // Post conditions
        Assert.assertNotNull(moviesNotPaginated);
        Assert.assertEquals(4, moviesNotPaginated.size());
        Assert.assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate, Movie.TABLE_NAME));
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testSearchMovies() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        helper.insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(123, 12);
        helper.insertMovie("MOVIE",124,"imdb124", LocalDate.of(2002,8,6));
        helper.insertMovieToMovieCategory(124, 12);
        helper.insertMovie("PELICULA",125,"imdb125", LocalDate.of(2003,8,6));
        helper.insertMovieToMovieCategory(125, 12);
        helper.insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(2005,8,6));
        helper.insertMovieToMovieCategory(126, 12);

        // Exercise
        final PaginatedCollection<Movie> movies = movieDao.searchMovies("Tit", NEWEST, 0, 2);

        // Post conditions
        Assert.assertEquals(2, movies.getTotalCount());
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testSearchMoviesByCategory() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        helper.insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(123, 12);
        helper.insertMovieToMovieCategory(123, ACTION_ID);
        helper.insertMovie("MOVIE",124,"imdb124", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(124, 12);
        helper.insertMovieToMovieCategory(124, 14);
        helper.insertMovie("PELICULA",125,"imdb125", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(125, 12);
        helper.insertMovieToMovieCategory(125, ACTION_ID);
        helper.insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(126, 12);
        helper.insertMovieToMovieCategory(126, 27);

        // Exercise
        final PaginatedCollection<Movie> movies = movieDao.searchMoviesByCategory("Tit", ACTION_NAME, NEWEST, 0, 2);

        // Post conditions
        Assert.assertEquals(1, movies.getTotalCount());
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testSearchMoviesByReleaseDate() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        helper.insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(123, 12);
        helper.insertMovie("MOVIE",124,"imdb124", LocalDate.of(2000,8,6));
        helper.insertMovieToMovieCategory(124, 12);
        helper.insertMovie("PELICULA",125,"imdb125", LocalDate.of(2002,8,6));
        helper.insertMovieToMovieCategory(125, 12);
        helper.insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(2004,8,6));
        helper.insertMovieToMovieCategory(126, 12);

        // Exercise
        final PaginatedCollection<Movie> movies = movieDao.searchMoviesByReleaseDate("Tit",
                LocalDate.ofYearDay(2000, 1),
                LocalDate.ofYearDay(2010, 1),
                NEWEST, 0, 2);

        // Post conditions
        Assert.assertEquals(1, movies.getTotalCount());
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testSearchMoviesByCategoryAndReleaseDate() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        helper.insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        helper.insertMovieToMovieCategory(123, 12);
        helper.insertMovieToMovieCategory(123, ACTION_ID);
        helper.insertMovie("MOVIE",124,"imdb124", LocalDate.of(2000,8,6));
        helper.insertMovieToMovieCategory(124, 12);
        helper.insertMovieToMovieCategory(124, 14);
        helper.insertMovie("PELICULA",125,"imdb125", LocalDate.of(2002,8,6));
        helper.insertMovieToMovieCategory(125, 12);
        helper.insertMovieToMovieCategory(125, ACTION_ID);
        helper.insertMovie("titulo DE PELICULA",126,"imdb126",LocalDate.of(2004,8,6));
        helper.insertMovieToMovieCategory(126, 12);
        helper.insertMovieToMovieCategory(126, 27);
        helper.insertMovie("TITULO DE MOVIE",127,"imdb127", LocalDate.of(2006,8,6));
        helper.insertMovieToMovieCategory(127, 12);
        helper.insertMovieToMovieCategory(127, ACTION_ID);

        // Exercise
        final PaginatedCollection<Movie> movies = movieDao.searchMoviesByCategoryAndReleaseDate("Tit", ACTION_NAME,
                LocalDate.of(2000,8,6),
                LocalDate.of(2010,8,6),
                NEWEST, 0, 2);

        // Post conditions
        Assert.assertEquals(1, movies.getTotalCount());
    }
}