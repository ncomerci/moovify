//import Config.TestConfig;
//import ar.edu.itba.paw.models.Movie;
//import ar.edu.itba.paw.models.MovieCategory;
//import ar.edu.itba.paw.persistence.MovieDaoImpl;
//import ar.edu.itba.paw.persistence.TableNames;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.jdbc.JdbcTestUtils;
//
//import javax.sql.DataSource;
//import java.sql.Timestamp;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestConfig.class)
//public class MovieDaoImplTest {
//
//    private static final String TITLE = "MovieTest";
//    private static final long TMDB_ID = 420;
//    private static final String IMDB_ID = "t1541";
//    private static final LocalDate RELEASE_DATE = LocalDate.now();
//    private static final float POPULARITY = 12.562F;
//    private static final float VOTE_AVERAGE = 1.562F;
//    private static final String OVERVIEW = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n";
//    private static final float RUNTIME = 104F;
//    private static final String ORIGINAL_LANGUAGE = "en";
//    private static final String ORIGINAL_TITLE = "MovieTest";
//    private static final Collection<Long> CATEGORIES = Arrays.asList(28L, 80L, 53L);
//
//    @Autowired
//    private MovieDaoImpl movieDao;
//
//    @Autowired
//    private DataSource ds;
//
//    private JdbcTemplate jdbcTemplate;
//    private SimpleJdbcInsert movieInsert;
//    private SimpleJdbcInsert movieToMovieCategoryInsert;
//
//    @Before
//    public void setUp() {
//        this.jdbcTemplate = new JdbcTemplate(ds);
//        this.movieInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName(TableNames.MOVIES.getTableName())
//                .usingGeneratedKeyColumns("movie_id");
//        this.movieToMovieCategoryInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName(TableNames.MOVIE_TO_MOVIE_CATEGORY.getTableName());
//    }
//
//
//
//    @Test
//    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    @Sql("classpath:movie_categories.sql")
//    public void testRegister() {
////        1. precondiciones
//        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.MOVIES.getTableName(), "tmdb_id = ?", TMDB_ID);
//
////        2. ejercitar
//        Movie movie = movieDao.register(TITLE, ORIGINAL_TITLE, TMDB_ID,IMDB_ID, ORIGINAL_LANGUAGE,
//                OVERVIEW, POPULARITY, RUNTIME, VOTE_AVERAGE, RELEASE_DATE, CATEGORIES);
//
////        3. post-condiciones
//        Assert.assertNotNull(movie);
//        assertValidMovie(movie);
//
//    }
//
//    @Test(expected = NullPointerException.class)
//    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    public void testInvalidRegister() {
//        movieDao.register(null, null,TMDB_ID, null,null, null,POPULARITY, RUNTIME,VOTE_AVERAGE, null,null);
//    }
//
////    @Test(expected = DuplicateKeyException.class)
////    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
////    public void testRegisterAlreadyExists() {
//////        1. precondiciones
////        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.MOVIES.getTableName(), "title = ?", MOVIE_TITLE);
////        HashMap<String, Object> map = new HashMap<>();
////        map.put("creation_date", Timestamp.valueOf(LocalDateTime.now()));
////        map.put("title", MOVIE_TITLE);
////        map.put("premier_date", RELEASE_DATE);
////        jdbcInsert.execute(map);
////
//////        2. ejercitar
////        movieDao.register(MOVIE_TITLE, RELEASE_DATE);
////
//////        3. post-condiciones
////        Assert.assertEquals(1,
////                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.MOVIES.getTableName(), "tmdb_id = " + getQuoted(String.valueOf(TMDB_ID)))
////        );
////
////    }
//
//    @Test
//    @Sql("classpath:movie_categories.sql")
//    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    public void testValidFindById() {
////        1. precondiciones
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("creation_date", Timestamp.valueOf(LocalDateTime.now()));
//        map.put("release_date", RELEASE_DATE);
//        map.put("title", TITLE);
//        map.put("original_title", ORIGINAL_TITLE);
//        map.put("tmdb_id", TMDB_ID);
//        map.put("imdb_id", IMDB_ID);
//        map.put("original_language", ORIGINAL_LANGUAGE);
//        map.put("overview", OVERVIEW);
//        map.put("popularity", POPULARITY);
//        map.put("runtime", RUNTIME);
//        map.put("vote_average", VOTE_AVERAGE);
//        Number key = movieInsert.executeAndReturnKey(map);
//
//        map = new HashMap<>();
//        for(Long cId : CATEGORIES){
//            map.put("tmdb_category_id", cId);
//            map.put("tmdb_id", TMDB_ID);
//            movieToMovieCategoryInsert.execute(map);
//        }
//
////        2. ejercitar
//        Optional<Movie> movie = movieDao.findById(key.longValue());
//
////        3. post-condiciones
//        Assert.assertTrue(movie.isPresent());
//        assertValidMovie(movie.get());
//    }
//
////    TODO: habria que repensar estos test
////    @Test
////    @Sql("classpath:test_inserts.sql")
////    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
////    public void testFindMoviesByPostId() {
//////        1. precondiciones
////
//////        2. ejercitar
////        ArrayList<Movie> movies = (ArrayList<Movie>) movieDao.findMoviesByPostId(1);
////
//////        3. post-condiciones
////        Assert.assertNotNull(movies);
////        Assert.assertEquals(JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POST_MOVIE.getTableName(), "post_id = 1"),
////                movies.size());
////    }
////
////    @Test
////    @Sql("classpath:test_inserts.sql")
////    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
////    public void testGetAllMovies() {
//////        1. precondiciones
////
//////        2. ejercitar
////        Collection<Movie> movies = movieDao.getAllMovies();
////
//////        3. post-condiciones
////        Assert.assertNotNull(movies);
////        Assert.assertEquals(JdbcTestUtils.countRowsInTable(jdbcTemplate, TableNames.MOVIES.getTableName()), movies.size());
////    }
//
//    private String getQuoted (String s){
//        return "'" + s + "'";
//    }
//
//    private void assertValidMovie(Movie movie) {
//        Assert.assertEquals(TITLE, movie.getTitle());
//        Assert.assertEquals(ORIGINAL_LANGUAGE, movie.getOriginalLanguage());
//        Assert.assertEquals(ORIGINAL_TITLE, movie.getOriginalTitle());
//        Assert.assertEquals(OVERVIEW, movie.getOverview());
//        Assert.assertEquals(POPULARITY, movie.getPopularity(), 1);
//        Assert.assertEquals(RUNTIME, movie.getRuntime(), 1);
//        Assert.assertEquals(VOTE_AVERAGE, movie.getVoteAverage(), 1);
//        Assert.assertEquals(TMDB_ID, movie.getTmdbId());
//        Assert.assertEquals(IMDB_ID, movie.getImdbId());
//        Assert.assertEquals(RELEASE_DATE, movie.getReleaseDate());
//        Assert.assertArrayEquals(movie.getCategories().stream().map(MovieCategory::getTmdb_id).sorted().toArray(),CATEGORIES.stream().sorted().toArray());
//        Assert.assertEquals(1,
//                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.MOVIES.getTableName(), "tmdb_id = " + getQuoted(String.valueOf(TMDB_ID)))
//        );
//    }
//}
