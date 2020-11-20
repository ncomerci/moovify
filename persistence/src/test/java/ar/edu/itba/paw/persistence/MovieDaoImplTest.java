package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class MovieDaoImplTest {
    private static final Map<String, Object> MOVIE_ROW = new HashMap<>();
    private static final Map<String, Object> MOVIE_TO_MOVIE_CATEGORY_ROW = new HashMap<>();
    private static final MovieDao.SortCriteria DEFAULT_SORT_CRITERIA = MovieDao.SortCriteria.NEWEST;
    private static final int INVALID_PAGE_NUMBER = -1;
    private static final int INVALID_PAGE_SIZE = -1;

    private static final int ACTION_ID = 28;
    private static final String ACTION_NAME = "action";

    private static long movieIdCount = 0;

    @Autowired
    private MovieDao movieDao;
    
    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert movieJdbcInsert;
    private SimpleJdbcInsert movieToMovieInsert;
    private SimpleJdbcInsert postMovieInsert;

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
                .withTableName(Movie.TABLE_NAME);
        this.movieToMovieInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(Movie.MOVIE_TO_MOVIE_CATEGORY_TABLE_NAME);
        mapInitializer();

        this.postMovieInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(Post.POST_MOVIE_TABLE_NAME);
    }

//    @Rollback
//    @Test
//    public void testRegister() {
//        //        1. precondiciones
//        final String title = "title";
//        final String originalTitle = "originalTitle";
//        final String originalLanguage = "ES";
//
//        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, Movie.TABLE_NAME, "title = ? AND original_title = ? AND original_language = ?", title, originalTitle, originalLanguage);
//
////        2. ejercitar
//        Movie movie =  movieDao.register(title, originalTitle, 12, "", originalLanguage, "", 1.5f, 1.5f, 1.5f, LocalDate.now(), Collections.singleton(12L));
//
////        3. post-condiciones
//        Assert.assertNotNull(movie);
//        final String whereClause = String.format("title = '%s' AND original_title = '%s' AND original_language = '%s'", title, originalTitle, originalLanguage);
//        Assert.assertEquals(1,
//                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, Movie.TABLE_NAME, whereClause)
//        );
//    }

//    @Test(expected = NullPointerException.class)
//    public void testInvalidRegister() {
//        movieDao.register(null, null, 0, null, null, null, 0, 0, 0, null, null);
//    }

    @Test
    public void testFindMovieById() {
//        1. precondiciones
        long id = movieIdCount++;

        MOVIE_ROW.put("movie_id", id);
        movieJdbcInsert.execute(MOVIE_ROW);

        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, Movie.MOVIE_TO_MOVIE_CATEGORY_TABLE_NAME,
                "tmdb_category_id = ? AND tmdb_id = ?", MOVIE_TO_MOVIE_CATEGORY_ROW.get("tmdb_category_id"),  MOVIE_ROW.get("tmdb_id"));

        movieToMovieInsert.execute(MOVIE_TO_MOVIE_CATEGORY_ROW);

//        2. ejercitar
        final Optional<Movie> movie = movieDao.findMovieById(id);

//        3. post-condiciones
        Assert.assertTrue(movie.isPresent());
        Assert.assertEquals(movie.get().getId(), id);
        final String whereClause = String.format("movie_id = %d AND title = '%s' AND original_title = '%s'", id, MOVIE_ROW.get("title"), MOVIE_ROW.get("original_title"));
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, Movie.TABLE_NAME, whereClause)
        );
    }

//    @Rollback
//    @Test
//    public void testFindMovieByPost() {
//
//        //Requires users with ID 1, 2 and 3.
//        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);
//
//        long movieId1 = insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
//        insertMovieToMovieCategory(123, 12);
//        insertPostMovie(1, movieId1);
//        insertPostMovie(2, movieId1);
//
//        long movieId2 = insertMovie("MOVIE",124,"imdb124", LocalDate.of(2002,8,6));
//        insertMovieToMovieCategory(124, 12);
//        insertPostMovie(1, movieId2);
//
//        long movieId3 = insertMovie("PELICULA",125,"imdb125", LocalDate.of(2003,8,6));
//        insertMovieToMovieCategory(125, 12);
//
//        long movieId4 = insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(2005,8,6));
//        insertMovieToMovieCategory(126, 12);
//        insertPostMovie(2, movieId4);
//        insertPostMovie(3, movieId4);
//
//        final Collection<Movie> movies = movieDao.findMoviesByPost(Mockito.when(Mockito.mock(Post.class).getId()).thenReturn(1L).getMock());
//
//        Assert.assertEquals(2, movies.size());
//    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testGetAllMoviesNotPaginated() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        long id = movieIdCount++;

        MOVIE_ROW.put("movie_id", id);

        movieJdbcInsert.execute(MOVIE_ROW);
        movieToMovieInsert.execute(MOVIE_TO_MOVIE_CATEGORY_ROW);

        int[] aux_ids = {2, 3, 4};
        long[] ids = new long[3];

        for(int i = 0; i < aux_ids.length ; i++) {
            MOVIE_ROW.put("tmdb_id", aux_ids[i]);
            MOVIE_ROW.put("imdb_id", String.format("%d", aux_ids[i]));

            ids[i] = movieIdCount++;
            MOVIE_ROW.put("movie_id", ids[i]);
             movieJdbcInsert.execute(MOVIE_ROW);

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
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, Movie.TABLE_NAME, whereClause)
        );
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testGetAllMoviesNewest() {

        // Requires users with ID 1, 2 and 3.
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        insertMovieToMovieCategory(123, 12);
        insertMovie("MOVIE",124,"imdb124", LocalDate.of(2002,8,6));
        insertMovieToMovieCategory(124, 12);
        insertMovie("PELICULA",125,"imdb125", LocalDate.of(2003,8,6));
        insertMovieToMovieCategory(125, 12);
        insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(2005,8,6));
        insertMovieToMovieCategory(126, 12);

        final PaginatedCollection<Movie> movies = movieDao.getAllMovies(MovieDao.SortCriteria.NEWEST, 1, 2);

        Assert.assertEquals(4, movies.getTotalCount());
        Assert.assertArrayEquals(new Long[]{124L, 123L}, movies.getResults().stream().map(Movie::getTmdbId).toArray());
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testGetAllMoviesOldest() {

        //Requires users with ID 1, 2 and 3.
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        insertMovieToMovieCategory(123, 12);
        insertMovie("MOVIE",124,"imdb124", LocalDate.of(2002,8,6));
        insertMovieToMovieCategory(124, 12);
        insertMovie("PELICULA",125,"imdb125", LocalDate.of(2003,8,6));
        insertMovieToMovieCategory(125, 12);
        insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(2005,8,6));
        insertMovieToMovieCategory(126, 12);

        final PaginatedCollection<Movie> movies = movieDao.getAllMovies(MovieDao.SortCriteria.OLDEST, 1, 2);

        Assert.assertEquals(4, movies.getTotalCount());
        Assert.assertArrayEquals(new Long[]{125L, 126L}, movies.getResults().stream().map(Movie::getTmdbId).toArray());
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testGetAllMoviesTitle() {

        //Requires users with ID 1, 2 and 3.
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        insertMovieToMovieCategory(123, 12);
        insertMovie("MOVIE",124,"imdb124", LocalDate.of(2002,8,6));
        insertMovieToMovieCategory(124, 12);
        insertMovie("ACE VENTURA",125,"imdb125", LocalDate.of(2003,8,6));
        insertMovieToMovieCategory(125, 12);
        insertMovie("ZORG",126,"imdb126", LocalDate.of(2005,8,6));
        insertMovieToMovieCategory(126, 12);

        final PaginatedCollection<Movie> movies = movieDao.getAllMovies(MovieDao.SortCriteria.TITLE, 1, 2);

        Assert.assertEquals(4, movies.getTotalCount());
        Assert.assertArrayEquals(new Long[]{123L, 126L}, movies.getResults().stream().map(Movie::getTmdbId).toArray());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:post1.sql")
    @Sql("classpath:post2.sql")
    @Sql("classpath:post3.sql")
    @Sql("classpath:movie-categories.sql")
    public void testGetAllMoviesPostCount() {

        //Requires users with ID 1, 2 and 3.
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        long movieId1 = insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        insertMovieToMovieCategory(123, 12);
        insertPostMovie(1, movieId1);
        insertPostMovie(2, movieId1);

        long movieId2 = insertMovie("MOVIE",124,"imdb124", LocalDate.of(2002,8,6));
        insertMovieToMovieCategory(124, 12);
        insertPostMovie(1, movieId2);

        long movieId3 = insertMovie("PELICULA",125,"imdb125", LocalDate.of(2003,8,6));
        insertMovieToMovieCategory(125, 12);

        long movieId4 = insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(2005,8,6));
        insertMovieToMovieCategory(126, 12);
        insertPostMovie(1, movieId4);
        insertPostMovie(2, movieId4);
        insertPostMovie(3, movieId4);

        final PaginatedCollection<Movie> movies = movieDao.getAllMovies(MovieDao.SortCriteria.POST_COUNT, 1, 2);

        Assert.assertEquals(4, movies.getTotalCount());
        Assert.assertArrayEquals(new Long[]{124L, 125L}, movies.getResults().stream().map(Movie::getTmdbId).toArray());
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testGetAllMoviesEmptyPage() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        insertMovieToMovieCategory(123, 12);
        insertMovie("MOVIE",124,"imdb124", LocalDate.of(2002,8,6));
        insertMovieToMovieCategory(124, 12);
        insertMovie("PELICULA",125,"imdb125", LocalDate.of(2003,8,6));
        insertMovieToMovieCategory(125, 12);
        insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(2005,8,6));
        insertMovieToMovieCategory(126, 12);

        final PaginatedCollection<Movie> movies = movieDao.getAllMovies(MovieDao.SortCriteria.NEWEST, 10, 10);

        Assert.assertEquals(4, movies.getTotalCount());
        Assert.assertEquals(0, movies.getResults().size());
    }

    @Test(expected = RuntimeException.class)
    public void testGetAllMoviesInvalidArgs() {

        movieDao.getAllMovies(null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }


    @Test
    @Sql("classpath:movie-categories.sql")
    public void testSearchMovies() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        insertMovieToMovieCategory(123, 12);
        insertMovie("MOVIE",124,"imdb124", LocalDate.of(2002,8,6));
        insertMovieToMovieCategory(124, 12);
        insertMovie("PELICULA",125,"imdb125", LocalDate.of(2003,8,6));
        insertMovieToMovieCategory(125, 12);
        insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(2005,8,6));
        insertMovieToMovieCategory(126, 12);

        final PaginatedCollection<Movie> movies = movieDao.searchMovies("Tit", DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(2, movies.getTotalCount());
    }

    @Test(expected = RuntimeException.class)
    public void testSearchmoviesInvalidArgs() {

        movieDao.searchMovies(null, null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testSearchMoviesByCategory() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        insertMovieToMovieCategory(123, 12);
        insertMovieToMovieCategory(123, ACTION_ID);
        insertMovie("MOVIE",124,"imdb124", LocalDate.of(1998,8,6));
        insertMovieToMovieCategory(124, 12);
        insertMovieToMovieCategory(124, 14);
        insertMovie("PELICULA",125,"imdb125", LocalDate.of(1998,8,6));
        insertMovieToMovieCategory(125, 12);
        insertMovieToMovieCategory(125, ACTION_ID);
        insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(1998,8,6));
        insertMovieToMovieCategory(126, 12);
        insertMovieToMovieCategory(126, 27);

        final PaginatedCollection<Movie> movies = movieDao.searchMoviesByCategory("Tit", ACTION_NAME, DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(1, movies.getTotalCount());
    }

    @Test(expected = RuntimeException.class)
    public void testSearchMoviesByCategoryInvalidArgs() {

        movieDao.searchMoviesByCategory(null, null, null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testSearchMoviesByReleaseDate() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        insertMovieToMovieCategory(123, 12);
        insertMovie("MOVIE",124,"imdb124", LocalDate.of(2000,8,6));
        insertMovieToMovieCategory(124, 12);
        insertMovie("PELICULA",125,"imdb125", LocalDate.of(2002,8,6));
        insertMovieToMovieCategory(125, 12);
        insertMovie("titulo DE PELICULA",126,"imdb126", LocalDate.of(2004,8,6));
        insertMovieToMovieCategory(126, 12);

        final PaginatedCollection<Movie> movies = movieDao.searchMoviesByReleaseDate("Tit",
                LocalDate.ofYearDay(2000, 1),
                LocalDate.ofYearDay(2010, 1),
                DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(1, movies.getTotalCount());
    }

    @Test(expected = RuntimeException.class)
    public void testSearchMoviesByReleaseDateInvalidArgs() {

        movieDao.searchMoviesByReleaseDate(null, null, null, null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    @Test
    @Sql("classpath:movie-categories.sql")
    public void testSearchMoviesByCategoryAndReleaseDate() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Movie.TABLE_NAME);

        insertMovie("TITULO",123,"imdb123", LocalDate.of(1998,8,6));
        insertMovieToMovieCategory(123, 12);
        insertMovieToMovieCategory(123, ACTION_ID);
        insertMovie("MOVIE",124,"imdb124", LocalDate.of(2000,8,6));
        insertMovieToMovieCategory(124, 12);
        insertMovieToMovieCategory(124, 14);
        insertMovie("PELICULA",125,"imdb125", LocalDate.of(2002,8,6));
        insertMovieToMovieCategory(125, 12);
        insertMovieToMovieCategory(125, ACTION_ID);
        insertMovie("titulo DE PELICULA",126,"imdb126",LocalDate.of(2004,8,6));
        insertMovieToMovieCategory(126, 12);
        insertMovieToMovieCategory(126, 27);
        insertMovie("TITULO DE MOVIE",127,"imdb127", LocalDate.of(2006,8,6));
        insertMovieToMovieCategory(127, 12);
        insertMovieToMovieCategory(127, ACTION_ID);

        final PaginatedCollection<Movie> movies = movieDao.searchMoviesByCategoryAndReleaseDate("Tit", ACTION_NAME,
                LocalDate.of(2000,8,6),
                LocalDate.of(2010,8,6),
                DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(1, movies.getTotalCount());
    }

    @Test(expected = RuntimeException.class)
    public void testSearchMoviesByCategoryAndReleaseDateInvalidArgs() {

        movieDao.searchMoviesByCategoryAndReleaseDate(null, null,null,
                null, null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    private long insertMovie(String title, long tmdbId, String imdbId, LocalDate releaseDate) {

        Map<String, Object> map = new HashMap<>();

        final long id = movieIdCount++;

        map.put("movie_id", id);
        map.put("creation_date", Timestamp.valueOf(LocalDateTime.of(2020,8,6,12,16)));
        map.put("release_date", releaseDate);
        map.put("title", title);
        map.put("original_title", "");
        map.put("tmdb_id", tmdbId);
        map.put("imdb_id", imdbId);
        map.put("original_language", "");
        map.put("overview", "");
        map.put("popularity", 5.2);
        map.put("runtime", 5.2);
        map.put("vote_average", 5.2);

        movieJdbcInsert.execute(map);

        return id;
    }

    private void insertMovieToMovieCategory(int movie_id, int category_id) {

        Map<String, Object> map = new HashMap<>();

        map.put("tmdb_category_id", category_id);
        map.put("tmdb_id", movie_id);

        movieToMovieInsert.execute(map);
    }

    private void insertPostMovie(long postId, long movieId) {

        Map<String, Object> map = new HashMap<>();
        map.put("post_id", postId);
        map.put("movie_id", movieId);

        postMovieInsert.execute(map);
    }
}