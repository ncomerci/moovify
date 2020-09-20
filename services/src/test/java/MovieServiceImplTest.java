import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.services.MovieServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MovieServiceImplTest {
    private static final String MOVIE_TITLE = "MovieTest";
    private static final LocalDate RELEASE_DATE = LocalDate.now();

    @Mock
    private MovieDao dao;

    @InjectMocks
    private final MovieServiceImpl movieService = new MovieServiceImpl();

    @Test
    public void testfindById() {
//        1. Setup: Establezco las pre-condiociones
        Optional<Movie> optional = Optional.empty();
        Mockito.when(dao.findById(Mockito.eq(1)))
                .thenReturn(optional);

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        Optional<Movie> movie = movieService.findById(1);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertNotNull(movie);
        Assert.assertFalse(movie.isPresent());
    }

    @Test
    public void testRegisterValidMovie() {
//        1. Setup: Establezco las pre-condiociones
        Mockito.when(dao.register(Mockito.eq(MOVIE_TITLE), Mockito.eq(RELEASE_DATE)))
                .thenReturn(Mockito.mock(Movie.class));

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        Movie movie = movieService.register(MOVIE_TITLE, RELEASE_DATE);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertNotNull(movie);
    }

    @Test
    public void testgetAllMovies() {
//        1. Setup: Establezco las pre-condiociones
        final ArrayList<Movie> list = new ArrayList<Movie>(){{
            add(Mockito.mock(Movie.class));
            add(Mockito.mock(Movie.class));
            add(Mockito.mock(Movie.class));
        }};

        Mockito.when(dao.getAllMovies())
                .thenReturn(list);

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        Collection<Movie> movies = movieService.getAllMovies();

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertNotNull(movies);
        Assert.assertEquals(3, movies.size());
    }

    @Test
    public void testfindMoviesByPostId() {
//        1. Setup: Establezco las pre-condiociones
        final ArrayList<Movie> list = new ArrayList<Movie>(){{
            add(Mockito.mock(Movie.class));
            add(Mockito.mock(Movie.class));
            add(Mockito.mock(Movie.class));
        }};

        Mockito.when(dao.findMoviesByPostId(1))
                .thenReturn(list);

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        Collection<Movie> movies = movieService.findMoviesByPostId(1);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertNotNull(movies);
        Assert.assertEquals(3, movies.size());
    }
}
