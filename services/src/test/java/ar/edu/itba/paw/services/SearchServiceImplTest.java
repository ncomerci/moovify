package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.MovieService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class SearchServiceImplTest {

    private static final int PAGE_SIZE = 5;
    private static final int PAGE_NUMBER = 0;
    private static final int TOTAL_COUNT = 10;
    private static final long CATEGORY_ID = 123L;
    private static final String ROLE = "user";
    private static final String QUERY = "Query";
    private static final String DECADE = "1920s";
    private static final String PERIOD = "pastWeek";
    private static final String MOVIE_CATEGORY = "action";
    private static final String POST_CATEGORY = "watchlist";
    private static final String DEFAULT_ORDER = "newest";

    @Mock
    private PostDao postDao;

    @Mock
    private MovieDao movieDao;

    @Mock
    private UserDao userDao;

    @Mock
    private CommentDao commentDao;

    @Mock
    private PostService postService;

    @Mock
    private MovieService movieService;

    @Mock
    private UserService userService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private final SearchServiceImpl searchService = new SearchServiceImpl(
            Mockito.when(Mockito.mock(PostCategoryDao.class)
                    .getAllPostCategories()).thenReturn(
                            Collections.singletonList(new PostCategory(CATEGORY_ID, LocalDateTime.now(), POST_CATEGORY))
            ).getMock(),

            Mockito.when(Mockito.mock(MovieCategoryDao.class)
                    .getAllCategories()).thenReturn(
                            Collections.singletonList(new MovieCategory(CATEGORY_ID, CATEGORY_ID, MOVIE_CATEGORY))
            ).getMock());



    @Test
    public void testSearchPosts() {

        Mockito.when(postService.getPostSortCriteria(DEFAULT_ORDER)).thenReturn(PostDao.SortCriteria.NEWEST);

        Mockito.when(postDao.searchPosts(
                Mockito.anyString(),
                Mockito.anyBoolean(),
                Mockito.any(PostDao.SortCriteria.class),
                Mockito.anyInt(),
                Mockito.anyInt())
        ).thenReturn(new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT));

        Optional<PaginatedCollection<Post>> posts = searchService.searchPosts(QUERY, null, null, true, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Mockito.verify(postDao).searchPosts(
                Mockito.anyString(),
                Mockito.anyBoolean(),
                Mockito.eq(PostDao.SortCriteria.NEWEST),
                Mockito.intThat(e -> e >= 0),
                Mockito.intThat(e -> e > 0)
        );

        Assert.assertTrue(posts.isPresent());
    }

    @Test
    public void testSearchPostsByCategory() {

        Mockito.when(postService.getPostSortCriteria(DEFAULT_ORDER)).thenReturn(PostDao.SortCriteria.NEWEST);

        Mockito.when(postDao.searchPostsByCategory(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean(),
                Mockito.any(PostDao.SortCriteria.class),
                Mockito.anyInt(),
                Mockito.anyInt())
        ).thenReturn(new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT));

        Optional<PaginatedCollection<Post>> posts = searchService.searchPosts(QUERY, POST_CATEGORY, null, true, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Mockito.verify(postDao).searchPostsByCategory(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean(),
                Mockito.eq(PostDao.SortCriteria.NEWEST),
                Mockito.intThat(e -> e >= 0),
                Mockito.intThat(e -> e > 0)
        );

        Assert.assertTrue(posts.isPresent());
    }

    @Test
    public void testSearchPostsByPeriod() {

        Mockito.when(postService.getPostSortCriteria(DEFAULT_ORDER)).thenReturn(PostDao.SortCriteria.NEWEST);

        Mockito.when(postDao.searchPostsOlderThan(
                Mockito.anyString(),
                Mockito.any(LocalDateTime.class),
                Mockito.anyBoolean(),
                Mockito.any(PostDao.SortCriteria.class),
                Mockito.anyInt(),
                Mockito.anyInt())
        ).thenReturn(new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT));

        Optional<PaginatedCollection<Post>> posts = searchService.searchPosts(QUERY, null, PERIOD, true, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Mockito.verify(postDao).searchPostsOlderThan(
                Mockito.anyString(),
                Mockito.any(LocalDateTime.class),
                Mockito.anyBoolean(),
                Mockito.eq(PostDao.SortCriteria.NEWEST),
                Mockito.intThat(e -> e >= 0),
                Mockito.intThat(e -> e > 0)
        );

        Assert.assertTrue(posts.isPresent());
    }

    @Test
    public void testSearchPostsByCategoryAndPeriod() {

        Mockito.when(postService.getPostSortCriteria(DEFAULT_ORDER)).thenReturn(PostDao.SortCriteria.NEWEST);

        Mockito.when(postDao.searchPostsByCategoryAndOlderThan(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(LocalDateTime.class),
                Mockito.anyBoolean(),
                Mockito.any(PostDao.SortCriteria.class),
                Mockito.anyInt(),
                Mockito.anyInt())
        ).thenReturn(new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT ));

        Optional<PaginatedCollection<Post>> posts = searchService.searchPosts(QUERY, POST_CATEGORY, PERIOD, true, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Mockito.verify(postDao).searchPostsByCategoryAndOlderThan(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(LocalDateTime.class),
                Mockito.anyBoolean(),
                Mockito.eq(PostDao.SortCriteria.NEWEST),
                Mockito.anyInt(),
                Mockito.anyInt()
        );

        Assert.assertTrue(posts.isPresent());
    }

    @Test
    public void testSearchPostsFail() {

        Optional<PaginatedCollection<Post>> posts = searchService.searchPosts(null, null, null, true, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Assert.assertFalse(posts.isPresent());
    }

    @Test
    public void testSearchMovies() {

        Mockito.when(movieService.getMovieSortCriteria(DEFAULT_ORDER)).thenReturn(MovieDao.SortCriteria.NEWEST);

        Mockito.when(movieDao.searchMovies(
                Mockito.anyString(),
                Mockito.any(MovieDao.SortCriteria.class),
                Mockito.anyInt(),
                Mockito.anyInt())
        ).thenReturn(new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT ));

        Optional<PaginatedCollection<Movie>> movie = searchService.searchMovies(QUERY, null, null, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Mockito.verify(movieDao).searchMovies(
                Mockito.anyString(),
                Mockito.eq(MovieDao.SortCriteria.NEWEST),
                Mockito.intThat(e -> e >= 0),
                Mockito.intThat(e -> e > 0)
        );

        Assert.assertTrue(movie.isPresent());
    }

    @Test
    public void testSearchMoviesByCategory() {

        Mockito.when(movieService.getMovieSortCriteria(DEFAULT_ORDER)).thenReturn(MovieDao.SortCriteria.NEWEST);

        Mockito.when(movieDao.searchMoviesByCategory(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(MovieDao.SortCriteria.class),
                Mockito.anyInt(),
                Mockito.anyInt())
        ).thenReturn(new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT ));

        Optional<PaginatedCollection<Movie>> movie = searchService.searchMovies(QUERY, MOVIE_CATEGORY, null, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Mockito.verify(movieDao).searchMoviesByCategory(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.eq(MovieDao.SortCriteria.NEWEST),
                Mockito.intThat(e -> e >= 0),
                Mockito.intThat(e -> e > 0)
        );

        Assert.assertTrue(movie.isPresent());
    }

    @Test
    public void testSearchMoviesByDecade() {

        Mockito.when(movieService.getMovieSortCriteria(DEFAULT_ORDER)).thenReturn(MovieDao.SortCriteria.NEWEST);

        Mockito.when(movieDao.searchMoviesByReleaseDate(
                Mockito.anyString(),
                Mockito.any(LocalDate.class),
                Mockito.any(LocalDate.class),
                Mockito.any(MovieDao.SortCriteria.class),
                Mockito.anyInt(),
                Mockito.anyInt())
        ).thenReturn(new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT ));

        Optional<PaginatedCollection<Movie>> movie = searchService.searchMovies(QUERY, null, DECADE, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Mockito.verify(movieDao).searchMoviesByReleaseDate(
                Mockito.anyString(),
                Mockito.any(LocalDate.class),
                Mockito.any(LocalDate.class),
                Mockito.eq(MovieDao.SortCriteria.NEWEST),
                Mockito.intThat(e -> e >= 0),
                Mockito.intThat(e -> e > 0)
        );

        Assert.assertTrue(movie.isPresent());
    }

    @Test
    public void testSearchMoviesByCategoryAndDecade() {

        Mockito.when(movieService.getMovieSortCriteria(DEFAULT_ORDER)).thenReturn(MovieDao.SortCriteria.NEWEST);

        Mockito.when(movieDao.searchMoviesByCategoryAndReleaseDate(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(LocalDate.class),
                Mockito.any(LocalDate.class),
                Mockito.any(MovieDao.SortCriteria.class),
                Mockito.anyInt(),
                Mockito.anyInt())
        ).thenReturn(new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT ));

        Optional<PaginatedCollection<Movie>> movie = searchService.searchMovies(QUERY, MOVIE_CATEGORY, DECADE, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Mockito.verify(movieDao).searchMoviesByCategoryAndReleaseDate(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(LocalDate.class),
                Mockito.any(LocalDate.class),
                Mockito.eq(MovieDao.SortCriteria.NEWEST),
                Mockito.intThat(e -> e >= 0),
                Mockito.intThat(e -> e > 0)
        );

        Assert.assertTrue(movie.isPresent());
    }

    @Test
    public void testSearchMoviesFail() {

        Optional<PaginatedCollection<Movie>> movie = searchService.searchMovies(null, null, null, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Assert.assertFalse(movie.isPresent());
    }

    @Test
    public void testSearchUsers() {

        Mockito.when(userService.getUserSortCriteria(DEFAULT_ORDER)).thenReturn(UserDao.SortCriteria.NEWEST);

        Mockito.when(userDao.searchUsers(
                Mockito.anyString(),
                Mockito.anyBoolean(),
                Mockito.any(UserDao.SortCriteria.class),
                Mockito.anyInt(),
                Mockito.anyInt())
        ).thenReturn(new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT ));

        Optional<PaginatedCollection<User>> user = searchService.searchUsers(QUERY, null, true, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Mockito.verify(userDao).searchUsers(
                Mockito.anyString(),
                Mockito.anyBoolean(),
                Mockito.eq(UserDao.SortCriteria.NEWEST),
                Mockito.intThat(e -> e >= 0),
                Mockito.intThat(e -> e > 0)
        );

        Assert.assertTrue(user.isPresent());
    }

    @Test
    public void testSearchUsersByRole() {

        Mockito.when(userService.getUserSortCriteria(DEFAULT_ORDER)).thenReturn(UserDao.SortCriteria.NEWEST);

        Mockito.when(userDao.searchUsersByRole(
                Mockito.anyString(),
                Mockito.any(Role.class),
                Mockito.anyBoolean(),
                Mockito.any(UserDao.SortCriteria.class),
                Mockito.anyInt(),
                Mockito.anyInt())
        ).thenReturn(new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT ));

        Optional<PaginatedCollection<User>> user = searchService.searchUsers(QUERY, ROLE, true, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Mockito.verify(userDao).searchUsersByRole(
                Mockito.anyString(),
                Mockito.any(Role.class),
                Mockito.anyBoolean(),
                Mockito.eq(UserDao.SortCriteria.NEWEST),
                Mockito.intThat(e -> e >= 0),
                Mockito.intThat(e -> e > 0)
        );

        Assert.assertTrue(user.isPresent());
    }

    @Test
    public void testSearchUsersFail() {

        Optional<PaginatedCollection<User>> user = searchService.searchUsers(null, null, true, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Assert.assertFalse(user.isPresent());
    }

    @Test
    public void testSearchComments() {

        Mockito.when(commentService.getCommentSortCriteria(DEFAULT_ORDER)).thenReturn(CommentDao.SortCriteria.NEWEST);

        Mockito.when(commentDao.searchComments(
                Mockito.anyString(),
                Mockito.anyBoolean(),
                Mockito.any(CommentDao.SortCriteria.class),
                Mockito.anyInt(),
                Mockito.anyInt())
        ).thenReturn(new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT ));

        Optional<PaginatedCollection<Comment>> comment = searchService.searchComments(QUERY, true, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Mockito.verify(commentDao).searchComments(
                Mockito.anyString(),
                Mockito.anyBoolean(),
                Mockito.eq(CommentDao.SortCriteria.NEWEST),
                Mockito.intThat(e -> e >= 0),
                Mockito.intThat(e -> e > 0)
        );

        Assert.assertTrue(comment.isPresent());
    }

    @Test
    public void testSearchDeletedPosts() {

        Mockito.when(postService.getPostSortCriteria(DEFAULT_ORDER)).thenReturn(PostDao.SortCriteria.NEWEST);

        Mockito.when(postDao.searchPosts(
                Mockito.anyString(),
                Mockito.anyBoolean(),
                Mockito.any(PostDao.SortCriteria.class),
                Mockito.anyInt(),
                Mockito.anyInt())
        ).thenReturn(new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT ));

        Optional<PaginatedCollection<Post>> posts = searchService.searchPosts(QUERY, null, null, false, DEFAULT_ORDER,  PAGE_NUMBER, PAGE_SIZE);

        Mockito.verify(postDao).searchPosts(
                Mockito.anyString(),
                Mockito.eq(false),
                Mockito.eq(PostDao.SortCriteria.NEWEST),
                Mockito.intThat(e -> e >= 0),
                Mockito.intThat(e -> e > 0)
        );

        Assert.assertTrue(posts.isPresent());
    }

    @Test
    public void testSearchDeletedPostsFail() {

        Optional<PaginatedCollection<Post>> posts = searchService.searchPosts(null, null, null, false, DEFAULT_ORDER, PAGE_NUMBER, PAGE_SIZE);

        Assert.assertFalse(posts.isPresent());
    }

    @Test
    public void testSearchDeletedComments() {

        Mockito.when(commentService.getCommentSortCriteria(DEFAULT_ORDER)).thenReturn(CommentDao.SortCriteria.NEWEST);

        Mockito.when(commentDao.searchComments(
                Mockito.anyString(),
                Mockito.anyBoolean(),
                Mockito.any(CommentDao.SortCriteria.class),
                Mockito.anyInt(),
                Mockito.anyInt())
        ).thenReturn(new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT ));

        Optional<PaginatedCollection<Comment>> comments = searchService.searchComments(QUERY, false, DEFAULT_ORDER,  PAGE_NUMBER, PAGE_SIZE);

        Mockito.verify(commentDao).searchComments(
                Mockito.anyString(),
                Mockito.eq(false),
                Mockito.eq(CommentDao.SortCriteria.NEWEST),
                Mockito.intThat(e -> e >= 0),
                Mockito.intThat(e -> e > 0)
        );

        Assert.assertTrue(comments.isPresent());
    }

    @Test
    public void testSearchDeletedCommentsFail() {

        Optional<PaginatedCollection<Comment>> comments = searchService.searchComments(null, false, DEFAULT_ORDER,  PAGE_NUMBER, PAGE_SIZE);

        Assert.assertFalse(comments.isPresent());
    }

    @Test
    public void testSearchDeletedUsers() {

        Mockito.when(userService.getUserSortCriteria(DEFAULT_ORDER)).thenReturn(UserDao.SortCriteria.NEWEST);

        Mockito.when(userDao.searchUsers(
                Mockito.anyString(),
                Mockito.anyBoolean(),
                Mockito.any(UserDao.SortCriteria.class),
                Mockito.anyInt(),
                Mockito.anyInt())
        ).thenReturn(new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT ));

        Optional<PaginatedCollection<User>> users = searchService.searchUsers(QUERY, null, false, DEFAULT_ORDER,  PAGE_NUMBER, PAGE_SIZE);

        Mockito.verify(userDao).searchUsers(
                Mockito.anyString(),
                Mockito.eq(false),
                Mockito.any(UserDao.SortCriteria.class),
                Mockito.intThat(e -> e >= 0),
                Mockito.intThat(e -> e > 0)
        );

        Assert.assertTrue(users.isPresent());
    }

    @Test
    public void testSearchDeletedUsersFail() {

        Optional<PaginatedCollection<User>> users = searchService.searchUsers(null, null, false, DEFAULT_ORDER,  PAGE_NUMBER, PAGE_SIZE);

        Assert.assertFalse(users.isPresent());
    }
}