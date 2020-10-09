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
    private static final long USER_ID = 1;
    private static final long CATEGORY_ID = 1;
    private static final PostCategory CATEGORY = Mockito.when(Mockito.mock(PostCategory.class).getId()).thenReturn(CATEGORY_ID).getMock();
    private static final User USER = Mockito.when(Mockito.mock(User.class).getId()).thenReturn(USER_ID).getMock();
    private static final Set<String> TAGS = Collections.singleton("Tag");
    private static final Set<Long> MOVIES = Collections.singleton(1L);
    private static final boolean ENABLE = true;
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 5;
    private static final long MOVIE_ID = 1L;

    @Autowired
    private PostDaoImpl postDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert postInsert;
    private SimpleJdbcInsert postMovieInsert;

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.postInsert = new SimpleJdbcInsert(ds)
                .withTableName(TableNames.POSTS.getTableName())
                .usingGeneratedKeyColumns("post_id");

        this.postMovieInsert = new SimpleJdbcInsert(ds)
                .withTableName(TableNames.POST_MOVIE.getTableName());
    }

    @Rollback
    @Test
    public void testRegister() {

        final Post post = postDao.register(TITLE, BODY, WORD_COUNT, CATEGORY, USER, TAGS, MOVIES, ENABLE);

        Assert.assertNotNull(post);

        final String whereClause = "post_id = " + post.getId() + " AND title = " + "'" + TITLE + "'" +
                " AND body = " + "'" + BODY + "'";

        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POSTS.getTableName(), whereClause)
        );

    }

    @Rollback
    @Test(expected = NullPointerException.class)
    public void testRegisterFail() {

        postDao.register(null, null, WORD_COUNT, null, null, null, null, false);
    }

    @Rollback
    @Test
    public void testDeletePost() {
        //Supongo que habia por lo menos uno

        final int countPreExecution = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POSTS.getTableName(), "enabled = true");

        postDao.deletePost(Mockito.when(Mockito.mock(Post.class).getId()).thenReturn(1L).getMock());

        final int countPostExecution = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POSTS.getTableName(), "enabled = true");

        Assert.assertEquals(countPreExecution - 1, countPostExecution);
    }

    @Rollback
    @Test
    public void testRestorePost() {
        //Supongo que habia por lo menos uno
        final int countPreExecution = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POSTS.getTableName(), "enabled = false");

        postDao.restorePost(Mockito.when(Mockito.mock(Post.class).getId()).thenReturn(4L).getMock());

        final int countPostExecution = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POSTS.getTableName(), "enabled = false");

        Assert.assertEquals(countPreExecution - 1, countPostExecution);
    }

    @Test
    public void likePost() {
    }

    @Test
    public void removeLike() {
    }

    @Rollback
    @Test
    public void findPostById() {

        final long postId = insertPost(TITLE, USER_ID, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final boolean isPresent = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POSTS.getTableName(), "post_id = " + postId) == 1;

        final Optional<Post> post = postDao.findPostById(postId);

        Assert.assertNotNull(post);
        Assert.assertEquals(isPresent, post.isPresent());
    }

    @Rollback
    @Test
    public void findPostByIdBadId() {

        final Optional<Post> post = postDao.findPostById(-1L);

        Assert.assertNotNull(post);
        Assert.assertFalse(post.isPresent());
    }

    @Rollback
    @Test
    public void findPostsByMovie() {

        for(int i = 0; i < 10; i++){
            long postId = insertPost(TITLE, USER_ID, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
            insertPostMovie(postId, MOVIE_ID);
        }

        final Movie movieMock = Mockito.when(Mockito.mock(Movie.class).getId()).thenReturn(MOVIE_ID).getMock();

        final int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,TableNames.POST_MOVIE.getTableName(), "movie_id = " + MOVIE_ID);

        final PaginatedCollection<Post> posts = postDao.findPostsByMovie(movieMock, PostDao.SortCriteria.NEWEST, PAGE_NUMBER, PAGE_SIZE);

        Assert.assertNotNull(posts);
        Assert.assertEquals(count, posts.getTotalCount());
    }

    @Rollback
    @Test
    public void findPostsByUser() {

        for(int i = 0; i < 10; i++){
            Map<String, Object> map = new HashMap<>();
            map.put("creation_date", Timestamp.valueOf(LocalDateTime.now()));
            map.put("title", TITLE);
            map.put("user_id", USER_ID);
            map.put("category_id", CATEGORY_ID);
            map.put("word_count", WORD_COUNT);
            map.put("body", BODY);
            map.put("enabled", ENABLE);
            postInsert.execute(map);
        }
//            insertPost(TITLE, USER_ID, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final User userMock = Mockito.when(Mockito.mock(User.class).getId()).thenReturn(USER_ID).getMock();

        final int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,TableNames.POSTS.getTableName(), "user_id = " + USER_ID);

        final PaginatedCollection<Post> posts = postDao.findPostsByUser(userMock, PostDao.SortCriteria.NEWEST, PAGE_NUMBER, PAGE_SIZE);

        Assert.assertNotNull(posts);
        Assert.assertEquals(count, posts.getTotalCount());
    }

    @Test
    public void getAllPosts() {
    }

    @Test
    public void getDeletedPosts() {
    }

    @Test
    public void searchPosts() {
    }

    @Test
    public void searchDeletedPosts() {
    }

    @Test
    public void searchPostsByCategory() {
    }

    @Test
    public void searchPostsOlderThan() {
    }

    @Test
    public void searchPostsByCategoryAndOlderThan() {
    }

    private long insertPost(String title, long userId, long categoryId, int wordCount, String body, boolean enable) {

        Map<String, Object> map = new HashMap<>();
        map.put("creation_date", Timestamp.valueOf(LocalDateTime.now()));
        map.put("title", title);
        map.put("user_id", userId);
        map.put("category_id", categoryId);
        map.put("word_count", wordCount);
        map.put("body", body);
        map.put("enabled", enable);

        return postInsert.executeAndReturnKey(map).longValue();
    }

    private void insertPostMovie(long postId, long movieId) {

        Map<String, Object> map = new HashMap<>();
        map.put("post_id", postId);
        map.put("movie_id", movieId);

        postMovieInsert.execute(map);
    }

}