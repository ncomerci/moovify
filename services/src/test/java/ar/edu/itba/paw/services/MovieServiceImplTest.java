package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.MovieCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.models.Post;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MovieServiceImplTest {

    private static final String TITLE = "Title";
    private static final String ORIGINAL_TITLE = "Original Title";
    private static final long TMDB_ID = 1234L;
    private static final String IMDB_ID = "imdb1234";
    private static final String LANGUAGE = "es";
    private static final String OVERVIEW = "Overview";
    private static final float POPULARITY = 1.3F;
    private static final float RUNTIME = 120F;
    private static final float VOTES = 5.2F;
    private static final LocalDate RELEASE_DATE = LocalDate.now();
    private static final Collection<Long> CATEGORIES = Collections.singletonList(123L);
    private static final long ID = 6L;
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 5;

    @Mock
    private MovieDao movieDao;

    @Mock
    private MovieCategoryDao movieCategoryDao;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    public void testRegister() {

        movieService.register(TITLE, ORIGINAL_TITLE, TMDB_ID, IMDB_ID, LANGUAGE, OVERVIEW,
                POPULARITY, RUNTIME, VOTES, RELEASE_DATE, CATEGORIES);
    }

    @Test
    public void testFindMovieById() {

        movieService.findMovieById(ID);
    }

    @Test
    public void testGetAllMovies() {

        movieService.getAllMovies(PAGE_NUMBER, PAGE_SIZE);
    }

    @Test
    public void testFindMoviesByPost() {

        movieService.findMoviesByPost(Mockito.mock(Post.class));
    }

    @Test
    public void testGetAllMoviesNotPaginated() {

        movieService.getAllMoviesNotPaginated();
    }

    @Test
    public void testGetAvailableCategories() {

        movieService.getAvailableCategories();
    }
}