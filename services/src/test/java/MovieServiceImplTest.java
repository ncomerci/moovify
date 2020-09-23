//import ar.edu.itba.paw.interfaces.persistence.MovieDao;
//import ar.edu.itba.paw.models.Movie;
//import ar.edu.itba.paw.services.MovieServiceImpl;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Optional;
//
//@RunWith(MockitoJUnitRunner.Silent.class)
//public class MovieServiceImplTest {
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
//    @Mock
//    private MovieDao dao;
//
//    @InjectMocks
//    private final MovieServiceImpl movieService = new MovieServiceImpl();
//
//    //TODO: estan andando pero no se si estan haciendo bien lo que deberian
//
//    @Test
//    public void testfindById() {
////        1. Setup: Establezco las pre-condiociones
//        Optional<Movie> optional = Optional.empty();
//        Mockito.when(dao.findById(Mockito.eq(1)))
//                .thenReturn(optional);
//
////        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
//        Optional<Movie> movie = movieService.findById(1);
//
////        3. Validaciones: Confirmo las postcondiciones
//        Assert.assertNotNull(movie);
//        Assert.assertFalse(movie.isPresent());
//    }
//
//    @Test
//    public void testRegisterValidMovie() {
////        1. Setup: Establezco las pre-condiociones
//        Mockito.when(dao.register(
//                Mockito.eq(TITLE), Mockito.eq(ORIGINAL_TITLE),
//                Mockito.eq(TMDB_ID), Mockito.eq(IMDB_ID),
//                Mockito.eq(ORIGINAL_LANGUAGE), Mockito.eq(OVERVIEW),
//                Mockito.eq(POPULARITY), Mockito.eq(RUNTIME),
//                Mockito.eq(VOTE_AVERAGE), Mockito.eq(RELEASE_DATE),
//                Mockito.eq(CATEGORIES))).thenReturn(Mockito.mock(Movie.class));
//
////        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
//        Movie movie = movieService.register(TITLE, ORIGINAL_TITLE, TMDB_ID,IMDB_ID, ORIGINAL_LANGUAGE,
//                OVERVIEW, POPULARITY, RUNTIME, VOTE_AVERAGE, RELEASE_DATE, CATEGORIES);
//
////        3. Validaciones: Confirmo las postcondiciones
//        Assert.assertNotNull(movie);
//    }
//
//    @Test
//    public void testgetAllMovies() {
////        1. Setup: Establezco las pre-condiociones
//        final ArrayList<Movie> list = new ArrayList<Movie>(){{
//            add(Mockito.mock(Movie.class));
//            add(Mockito.mock(Movie.class));
//            add(Mockito.mock(Movie.class));
//        }};
//
//        Mockito.when(dao.getAllMovies())
//                .thenReturn(list);
//
////        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
//        Collection<Movie> movies = movieService.getAllMovies();
//
////        3. Validaciones: Confirmo las postcondiciones
//        Assert.assertNotNull(movies);
//        Assert.assertEquals(3, movies.size());
//    }
//
//    @Test
//    public void testfindMoviesByPostId() {
////        1. Setup: Establezco las pre-condiociones
//        final ArrayList<Movie> list = new ArrayList<Movie>(){{
//            add(Mockito.mock(Movie.class));
//            add(Mockito.mock(Movie.class));
//            add(Mockito.mock(Movie.class));
//        }};
//
//        Mockito.when(dao.findMoviesByPostId(1))
//                .thenReturn(list);
//
////        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
//        Collection<Movie> movies = movieService.findMoviesByPostId(1);
//
////        3. Validaciones: Confirmo las postcondiciones
//        Assert.assertNotNull(movies);
//        Assert.assertEquals(3, movies.size());
//    }
//}
