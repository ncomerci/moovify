package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.InvalidMovieIdException;
import ar.edu.itba.paw.models.*;
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
import java.time.LocalDateTime;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class PostDaoImplTest {

    private static final String TITLE = "Title";
    private static final String BODY = "Body";
    private static final int WORD_COUNT = 1;
    private static final LocalDateTime CREATION_DATE = LocalDateTime.of(2020, 8,6, 11,55);
    private static final PostDao.SortCriteria DEFAULT_SORT_CRITERIA = PostDao.SortCriteria.NEWEST;
    private static final boolean ENABLE = true;
    private static final boolean NOT_ENABLE = false;

    private static final int UP_VOTE = 1;
    private static final int DOWN_VOTE = -1;

    private static final long USER_ID = 1;
    private static final long USER_ID2 = 2L;
    private static final User USER_MOCK = Mockito.when(Mockito.mock(User.class).getId()).thenReturn(USER_ID).getMock();

    private static final long CATEGORY_ID = 1;
    private static final String CATEGORY_NAME = "watchlist";
    private static final long CATEGORY_ID2 = 2L;
    private static final PostCategory CATEGORY = Mockito.when(Mockito.mock(PostCategory.class).getId()).thenReturn(CATEGORY_ID).getMock();

    private static final Set<String> TAGS = Collections.singleton("Tag");

    private static final long MOVIE_ID = 1L;
    private static final long MOVIE_ID2 = 2L;
    private static final Set<Long> MOVIES = Collections.singleton(1L);


    private static final int INVALID_PAGE_NUMBER = -1;
    private static final int INVALID_PAGE_SIZE = 0;
    private static final Movie MOVIE_MOCK = Mockito.when(Mockito.mock(Movie.class).getId()).thenReturn(MOVIE_ID).getMock();

    @Autowired
    private PostDaoImpl postDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert postInsert;
    private SimpleJdbcInsert postMovieInsert;
    private SimpleJdbcInsert postLikeInsert;

    @Before
    public void testSetUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.postInsert = new SimpleJdbcInsert(ds)
                .withTableName(TableNames.POSTS.getTableName())
                .usingGeneratedKeyColumns("post_id");

        this.postMovieInsert = new SimpleJdbcInsert(ds)
                .withTableName(TableNames.POST_MOVIE.getTableName());

        this.postLikeInsert = new SimpleJdbcInsert(ds).withTableName(TableNames.POSTS_LIKES.getTableName());
    }

    @Rollback
    @Test
    public void testRegister() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        final Post post = postDao.register(TITLE, BODY, WORD_COUNT, CATEGORY, USER_MOCK, TAGS, MOVIES, ENABLE);

        final String whereClause = String.format("post_id = %d AND title = '%s' AND body = '%s'", post.getId(), TITLE, BODY);

        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POSTS.getTableName(), whereClause)
        );

    }

    @Rollback
    @Test(expected = InvalidMovieIdException.class)
    public void testRegisterInvalidMovies() {

        Set<Long> movies = new HashSet<>(MOVIES);
        movies.add(-1L);

        postDao.register(TITLE, BODY, WORD_COUNT, CATEGORY, USER_MOCK, TAGS, movies, ENABLE);
    }

    @Rollback
    @Test(expected = NullPointerException.class)
    public void testRegisterInvalidArgs() {

        postDao.register(null, null, WORD_COUNT, null, null, null, null, false);
    }

    // ===========================================================

    @Rollback
    @Test
    public void testDeletePost() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        final long postId = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final Post mockedPost = Mockito.when(Mockito.mock(Post.class).getId()).thenReturn(postId).getMock();

        postDao.deletePost(mockedPost);

        final int countPostExecution = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POSTS.getTableName(), "enabled = true");

        Assert.assertEquals(0, countPostExecution);
    }

    @Rollback
    @Test(expected = RuntimeException.class)
    public void testDeletePostInvalidArgs() {

        postDao.deletePost(null);

    }

    @Rollback
    @Test
    public void testRestorePost() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        final long postId = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        final Post mockedPost = Mockito.when(Mockito.mock(Post.class).getId()).thenReturn(postId).getMock();

        postDao.restorePost(mockedPost);

        final int countPostExecution = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POSTS.getTableName(), "enabled = true");

        Assert.assertEquals(1, countPostExecution);
    }

    @Rollback
    @Test(expected = RuntimeException.class)
    public void testRestorePostInvalidArgs() {

        postDao.restorePost(null);
    }

    // ===========================================================

    @Rollback
    @Test
    public void testLikePost() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        final long postId = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final Post mockedPost = Mockito.when(Mockito.mock(Post.class).getId()).thenReturn(postId).getMock();

        postDao.likePost(mockedPost, USER_MOCK, UP_VOTE);

        final int countPostExecution = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POSTS_LIKES.getTableName(), "post_id = " + postId);

        Assert.assertEquals(1, countPostExecution);
    }

    @Rollback
    @Test(expected = RuntimeException.class)
    public void testLikePostInvalidArgs() {

        postDao.likePost(null, null, 1);
    }

    @Rollback
    @Test
    public void testRemoveLike() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        final long postId = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPostLike(postId, USER_ID, DOWN_VOTE);
        final Post mockedPost = Mockito.when(Mockito.mock(Post.class).getId()).thenReturn(postId).getMock();

        postDao.removeLike(mockedPost, USER_MOCK);

        final int countPostExecution = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POSTS_LIKES.getTableName(), "post_id = " + postId);

        Assert.assertEquals(0, countPostExecution);
    }

    @Rollback
    @Test(expected = RuntimeException.class)
    public void testRemoveLikeInvalidArgs() {

        postDao.removeLike(null, null);
    }

    // ===========================================================

    @Rollback
    @Test
    public void testFindPostById() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        final long postId = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final Optional<Post> post = postDao.findPostById(postId);

        Assert.assertTrue(post.isPresent());
        Assert.assertEquals(postId, post.get().getId());
    }

    @Rollback
    @Test
    public void testFindPostByIdInvalidArgs() {

        final Optional<Post> post = postDao.findPostById(-1L);

        Assert.assertFalse(post.isPresent());
    }

    @Rollback
    @Test
    public void testFindPostsByMovie() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        final long post1 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPostMovie(post1, MOVIE_ID);
        final long post2 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPostMovie(post2, MOVIE_ID2);
        final long post3 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPostMovie(post3, MOVIE_ID);
        final long post4 = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPostMovie(post4, MOVIE_ID2);

        final PaginatedCollection<Post> posts = postDao.findPostsByMovie(MOVIE_MOCK, DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(2, posts.getTotalCount());
    }

    @Rollback
    @Test
    public void testFindPostsByUser() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost(TITLE, USER_ID2, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost(TITLE, USER_ID2, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.findPostsByUser(USER_MOCK, DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(2, posts.getTotalCount());
    }

    // ===========================================================
    // Buscando en la 2 pagina -> bien orden global
    // Buscando el orden en results -> verificamos orden de la pagina

    @Rollback
    @Test
    public void testGetAllPostsNewest() {

        // Requires users with ID 1, 2 and 3.
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        final long post1 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post2 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post3 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post4 = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(PostDao.SortCriteria.NEWEST, 1, 2);

        Assert.assertEquals(4, posts.getTotalCount());
        Assert.assertArrayEquals(new Long[]{post2, post4}, posts.getResults().stream().map(Post::getId).toArray());
    }

    @Rollback
    @Test
    public void testGetAllPostsOldest() {

        //Requires users with ID 1, 2 and 3.
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        final long post1 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post2 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post3 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post4 = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(PostDao.SortCriteria.OLDEST, 1, 2);

        Assert.assertEquals(4, posts.getTotalCount());
        Assert.assertArrayEquals(new Long[]{post3, post1}, posts.getResults().stream().map(Post::getId).toArray());
    }

    @Rollback
    @Test
    public void testGetAllPostsHottest() {

        //Requires users with ID 1, 2 and 3.
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        final long post1 = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPostLike(post1, 1L,UP_VOTE);
        insertPostLike(post1, 2L,UP_VOTE);
        final long post2 = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPostLike(post2, 1L,UP_VOTE);
        insertPostLike(post2, 2L,UP_VOTE);
        insertPostLike(post2, 3L,UP_VOTE);
        final long post3 = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPostLike(post3, 1L,UP_VOTE);
        insertPostLike(post3, 2L,DOWN_VOTE);
        insertPostLike(post3, 3L,DOWN_VOTE);
        final long post4 = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(PostDao.SortCriteria.HOTTEST, 1, 2);

        Assert.assertEquals(4, posts.getTotalCount());
        Assert.assertArrayEquals(new Long[]{post4, post3}, posts.getResults().stream().map(Post::getId).toArray());
    }

    @Rollback
    @Test
    public void testGetAllPostsEmptyPage() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(PostDao.SortCriteria.NEWEST, 10, 10);

        Assert.assertEquals(4, posts.getTotalCount());
        Assert.assertEquals(0, posts.getResults().size());
    }

    @Rollback
    @Test
    public void testGetAllPostsExcludingNotEnabled() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(PostDao.SortCriteria.NEWEST, 0, 2);

        Assert.assertEquals(2, posts.getTotalCount());
    }

    @Rollback
    @Test(expected = RuntimeException.class)
    public void testGetAllPostsInvalidArgs() {

        postDao.getAllPosts(null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    // ===========================================================

    @Rollback
    @Test
    public void testGetDeletedPostsExcludingEnabled() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);

        final PaginatedCollection<Post> posts = postDao.getDeletedPosts(PostDao.SortCriteria.NEWEST, 0, 2);

        Assert.assertEquals(3, posts.getTotalCount());
    }

    @Rollback
    @Test(expected = RuntimeException.class)
    public void testGetDeletedPostsInvalidArgs() {

        postDao.getDeletedPosts(null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    // ===========================================================

    @Rollback
    @Test
    public void testSearchPosts() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        insertPost("Title", USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost("Titulito", USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost("Tetle", USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost("Nombre", USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.searchPosts("Tit", DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(2, posts.getTotalCount());
    }

    @Rollback
    @Test(expected = RuntimeException.class)
    public void testSearchPostsInvalidArgs() {

        postDao.searchPosts(null, null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    @Rollback
    @Test
    public void testSearchDeletedPosts() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        insertPost("Title", USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        insertPost("Titulito", USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost("Tetle", USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        insertPost("Nombre", USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.searchPosts("Tit", DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(1, posts.getTotalCount());
    }

    @Rollback
    @Test(expected = RuntimeException.class)
    public void testSearchDeletedPostsInvalidArgs() {

        postDao.searchDeletedPosts(null, null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    @Rollback
    @Test
    public void testSearchPostsByCategory() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        insertPost("Title", USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost("Titulito", USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID2, WORD_COUNT, BODY, ENABLE);
        insertPost("Tetle", USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost("Nombre", USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.searchPostsByCategory("Tit", CATEGORY_NAME, DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(1, posts.getTotalCount());
    }

    @Rollback
    @Test(expected = RuntimeException.class)
    public void testSearchPostsByCategoryInvalidArgs() {

        postDao.searchPostsByCategory(null, null, null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    @Rollback
    @Test
    public void testSearchPostsOlderThan() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        final long post1 = insertPost("Title", USER_ID, CREATION_DATE.minusHours(15), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post2 = insertPost("Titulito", USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post3 = insertPost("Tetle", USER_ID, CREATION_DATE.minusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post4 = insertPost("Nombre", USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.searchPostsOlderThan("Tit", CREATION_DATE, DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(1, posts.getTotalCount());
        Assert.assertEquals(post2, posts.getResults().stream().findFirst().get().getId());
    }

    @Rollback
    @Test(expected = RuntimeException.class)
    public void testSearchPostsOlderThanInvalidArgs() {

        postDao.searchPostsOlderThan(null, null, null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    @Rollback
    @Test
    public void testSearchPostsByCategoryAndOlderThan() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.POSTS.getTableName());

        final long post1 = insertPost("Title", USER_ID, CREATION_DATE.minusHours(15), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post2 = insertPost("Titulito", USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post3 = insertPost("Nombre", USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post4 = insertPost("Tittle", USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID2, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.searchPostsByCategoryAndOlderThan("Tit", CATEGORY_NAME,
                CREATION_DATE, DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(1, posts.getTotalCount());
        Assert.assertEquals(post2, posts.getResults().stream().findFirst().get().getId());
    }

    @Rollback
    @Test(expected = RuntimeException.class)
    public void testSearchPostsByCategoryAndOlderThanInvalidArgs() {

        postDao.searchPostsByCategoryAndOlderThan(null, null, null, null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    // ===========================================================

    private long insertPost(String title, long userId, LocalDateTime creationDate, long categoryId, int wordCount, String body, boolean enable) {

        final long postId;

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("creation_date", Timestamp.valueOf(creationDate));
        postMap.put("title", title);
        postMap.put("user_id", userId);
        postMap.put("category_id", categoryId);
        postMap.put("word_count", wordCount);
        postMap.put("body", body);
        postMap.put("enabled", enable);

        postId = postInsert.executeAndReturnKey(postMap).longValue();

        return postId;

    }

    private void insertPostMovie(long postId, long movieId) {

        Map<String, Object> map = new HashMap<>();
        map.put("post_id", postId);
        map.put("movie_id", movieId);

        postMovieInsert.execute(map);
    }

    private void insertPostLike(long postId, long userId, int value) {

        Map<String, Object> map = new HashMap<>();
        map.put("post_id", postId);
        map.put("user_id", userId);
        map.put("value", value);

        postLikeInsert.execute(map);
    }

}