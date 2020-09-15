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

@RunWith(MockitoJUnitRunner.class)
public class MovieServiceImplTest {
    private static final String MOVIE_TITLE = "MovieTest";
    private static final LocalDate RELEASE_DATE = LocalDate.now();

    @Mock
    private MovieDao dao;

    @InjectMocks
    private final MovieServiceImpl movieService = new MovieServiceImpl();

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
}
