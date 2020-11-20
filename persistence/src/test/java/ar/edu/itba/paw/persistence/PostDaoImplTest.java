package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
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

    private static final long USER_ID = 1L;
    private static final long USER_ID2 = 2L;
    private static final long USER_ID3 = 3L;
    private static final User USER_MOCK = Mockito.when(Mockito.mock(User.class).getId()).thenReturn(USER_ID).getMock();

    private static final long CATEGORY_ID = 1;
    private static final String CATEGORY_NAME = "watchlist";
    private static final long CATEGORY_ID2 = 2L;
    private static final PostCategory CATEGORY = Mockito.when(Mockito.mock(PostCategory.class).getId()).thenReturn(CATEGORY_ID).getMock();

    private static final Set<String> TAGS = Collections.singleton("Tag");

    private static final long MOVIE_ID = 1L;
    private static final long MOVIE_ID2 = 2L;
    private static final Set<Long> MOVIES = Collections.singleton(1L);

    private static long postIdCount = 0;
    private static long postLikeIdCount = 0;

    private static final int INVALID_PAGE_NUMBER = -1;
    private static final int INVALID_PAGE_SIZE = 0;
    private static final Movie MOVIE_MOCK = Mockito.when(Mockito.mock(Movie.class).getId()).thenReturn(MOVIE_ID).getMock();

    @Autowired
    private PostDaoImpl postDao;

    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert postInsert;
    private SimpleJdbcInsert postMovieInsert;
    private SimpleJdbcInsert postLikeInsert;

    @Before
    public void testSetUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.postInsert = new SimpleJdbcInsert(ds)
                .withTableName(Post.TABLE_NAME);

        this.postMovieInsert = new SimpleJdbcInsert(ds)
                .withTableName(Post.POST_MOVIE_TABLE_NAME);

        this.postLikeInsert = new SimpleJdbcInsert(ds).withTableName(PostLike.TABLE_NAME);
    }

//    @Rollback
//    @Test
//    public void testRegister() {
//
//        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);
//
//        final Post post = postDao.register(TITLE, BODY, WORD_COUNT, CATEGORY, USER_MOCK, TAGS, MOVIES, ENABLE);
//
//        final String whereClause = String.format("post_id = %d AND title = '%s' AND body = '%s'", post.getId(), TITLE, BODY);
//
//        Assert.assertEquals(1,
//                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, Post.TABLE_NAME, whereClause)
//        );
//
//    }

    // The InvalidMovieIdException can only be detected in Postgresql environment
//    @Rollback
//    @Test(expected = DataIntegrityViolationException.class)
//    public void testRegisterInvalidMovies() {
//
//        Set<Long> movies = new HashSet<>(MOVIES);
//        movies.add(-1L);
//
//        postDao.register(TITLE, BODY, WORD_COUNT, CATEGORY, USER_MOCK, TAGS, movies, ENABLE);
//    }

//    @Rollback
//    @Test(expected = NullPointerException.class)
//    public void testRegisterInvalidArgs() {
//
//        postDao.register(null, null, WORD_COUNT, null, null, null, null, false);
//    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testFindPostById() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        final long postId = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final Optional<Post> post = postDao.findPostById(postId);

        Assert.assertTrue(post.isPresent());
        Assert.assertEquals(postId, post.get().getId());
    }

    @Test
    public void testFindPostByIdInvalidArgs() {

        final Optional<Post> post = postDao.findPostById(-1L);

        Assert.assertFalse(post.isPresent());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:movies.sql")
    public void testFindPostsByMovie() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

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

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:user2.sql")
    @Sql("classpath:categories.sql")
    public void testFindPostsByUser() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

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

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testGetAllPostsNewest() {

        // Requires users with ID 1, 2 and 3.
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        final long post1 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post2 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post3 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post4 = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(PostDao.SortCriteria.NEWEST, 1, 2);

        Assert.assertEquals(4, posts.getTotalCount());
        Assert.assertArrayEquals(new Long[]{post2, post4}, posts.getResults().stream().map(Post::getId).toArray());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testGetAllPostsOldest() {

        //Requires users with ID 1, 2 and 3.
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        final long post1 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post2 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post3 = insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post4 = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(PostDao.SortCriteria.OLDEST, 1, 2);

        Assert.assertEquals(4, posts.getTotalCount());
        Assert.assertArrayEquals(new Long[]{post3, post1}, posts.getResults().stream().map(Post::getId).toArray());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:user2.sql")
    @Sql("classpath:user3.sql")
    @Sql("classpath:categories.sql")
    public void testGetAllPostsHottest() {

        //Requires users with ID 1, 2 and 3.
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        final long post1 = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPostLike(post1, USER_ID, UP_VOTE);
        insertPostLike(post1, USER_ID2, UP_VOTE);
        final long post2 = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPostLike(post2, USER_ID, UP_VOTE);
        insertPostLike(post2, USER_ID2, UP_VOTE);
        insertPostLike(post2, USER_ID3, UP_VOTE);
        final long post3 = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPostLike(post3, USER_ID, UP_VOTE);
        insertPostLike(post3, USER_ID2, DOWN_VOTE);
        insertPostLike(post3, USER_ID3, DOWN_VOTE);
        final long post4 = insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(PostDao.SortCriteria.HOTTEST, 1, 2);

        Assert.assertEquals(4, posts.getTotalCount());
        Assert.assertArrayEquals(new Long[]{post4, post3}, posts.getResults().stream().map(Post::getId).toArray());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testGetAllPostsEmptyPage() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(PostDao.SortCriteria.NEWEST, 10, 10);

        Assert.assertEquals(4, posts.getTotalCount());
        Assert.assertEquals(0, posts.getResults().size());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testGetAllPostsExcludingNotEnabled() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(PostDao.SortCriteria.NEWEST, 0, 2);

        Assert.assertEquals(2, posts.getTotalCount());
    }

    @Test(expected = RuntimeException.class)
    public void testGetAllPostsInvalidArgs() {

        postDao.getAllPosts(null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    // ===========================================================

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testGetDeletedPostsExcludingEnabled() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost(TITLE, USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);

        final PaginatedCollection<Post> posts = postDao.getDeletedPosts(PostDao.SortCriteria.NEWEST, 0, 2);

        Assert.assertEquals(3, posts.getTotalCount());
    }

    @Test(expected = RuntimeException.class)
    public void testGetDeletedPostsInvalidArgs() {

        postDao.getDeletedPosts(null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    // ===========================================================

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testSearchPosts() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

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

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testSearchDeletedPosts() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        insertPost("Title", USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        insertPost("Titulito", USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost("Tetle", USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        insertPost("Nombre", USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.searchPosts("Tit", DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(1, posts.getTotalCount());
    }

    @Test(expected = RuntimeException.class)
    public void testSearchDeletedPostsInvalidArgs() {

        postDao.searchDeletedPosts(null, null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testSearchPostsByCategory() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        insertPost("Title", USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost("Titulito", USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID2, WORD_COUNT, BODY, ENABLE);
        insertPost("Tetle", USER_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        insertPost("Nombre", USER_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.searchPostsByCategory("Tit", CATEGORY_NAME, DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(1, posts.getTotalCount());
    }

    @Test(expected = RuntimeException.class)
    public void testSearchPostsByCategoryInvalidArgs() {

        postDao.searchPostsByCategory(null, null, null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testSearchPostsOlderThan() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        final long post1 = insertPost("Title", USER_ID, CREATION_DATE.minusHours(15), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post2 = insertPost("Titulito", USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post3 = insertPost("Tetle", USER_ID, CREATION_DATE.minusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post4 = insertPost("Nombre", USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.searchPostsOlderThan("Tit", CREATION_DATE, DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(1, posts.getTotalCount());
        Assert.assertTrue(posts.getResults().stream().findFirst().isPresent());
        Assert.assertEquals(post2, posts.getResults().stream().findFirst().get().getId());
    }

    @Test(expected = RuntimeException.class)
    public void testSearchPostsOlderThanInvalidArgs() {

        postDao.searchPostsOlderThan(null, null, null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testSearchPostsByCategoryAndOlderThan() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        final long post1 = insertPost("Title", USER_ID, CREATION_DATE.minusHours(15), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post2 = insertPost("Titulito", USER_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post3 = insertPost("Nombre", USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post4 = insertPost("Tittle", USER_ID, CREATION_DATE.plusHours(10), CATEGORY_ID2, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.searchPostsByCategoryAndOlderThan("Tit", CATEGORY_NAME,
                CREATION_DATE, DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(1, posts.getTotalCount());
        Assert.assertTrue(posts.getResults().stream().findFirst().isPresent());
        Assert.assertEquals(post2, posts.getResults().stream().findFirst().get().getId());
    }

    @Test(expected = RuntimeException.class)
    public void testSearchPostsByCategoryAndOlderThanInvalidArgs() {

        postDao.searchPostsByCategoryAndOlderThan(null, null, null, null, INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE);
    }

    // ===========================================================

    private long insertPost(String title, long userId, LocalDateTime creationDate, long categoryId, int wordCount, String body, boolean enable) {

        final long postId = ++postIdCount;

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("post_id", postId);
        postMap.put("body", body);
        postMap.put("creation_date", Timestamp.valueOf(creationDate));
        postMap.put("edited", false);
        postMap.put("enabled", enable);
        postMap.put("last_edited", null);
        postMap.put("title", title);
        postMap.put("word_count", wordCount);
        postMap.put("category_id", categoryId);
        postMap.put("user_id", userId);

        postInsert.execute(postMap);

        return postId;

    }

    private void insertPostMovie(long postId, long movieId) {

        Map<String, Object> map = new HashMap<>();
        map.put("post_id", postId);
        map.put("movie_id", movieId);

        postMovieInsert.execute(map);
    }

    private void insertPostLike(long postId, long userId, int value) {

        final long id = ++postLikeIdCount;

        Map<String, Object> map = new HashMap<>();
        map.put("post_likes_id", id);
        map.put("post_id", postId);
        map.put("user_id", userId);
        map.put("value", value);

        postLikeInsert.execute(map);
    }

}