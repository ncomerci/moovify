package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

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

    private static final long USER1_ID = InsertHelper.USER1_ID;
    private static final long USER2_ID = InsertHelper.USER2_ID;
    private static final long USER3_ID = InsertHelper.USER3_ID;

    private static final long CATEGORY_ID = 1;
    private static final String CATEGORY_NAME = "watchlist";
    private static final long CATEGORY_ID2 = 2L;

    private static final long MOVIE1_ID = 1L;
    private static final long MOVIE2_ID = 2L;

    private static final int INVALID_PAGE_NUMBER = -1;
    private static final int INVALID_PAGE_SIZE = 0;

    @Autowired
    private PostDaoImpl postDao;

    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    private InsertHelper helper;

    @Before
    public void testSetUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.helper = new InsertHelper(jdbcTemplate);
    }

    @Test
    @Sql("classpath:categories.sql")
    @Sql("classpath:user1.sql")
    @Sql("classpath:movies.sql")
    public void testRegister() {

        // Pre conditions
        User user = em.find(User.class, USER1_ID);
        PostCategory category = em.find(PostCategory.class, CATEGORY_ID);
        Movie movie = em.find(Movie.class, MOVIE1_ID);

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        // Exercise
        final Post post = postDao.register(TITLE, BODY, WORD_COUNT, category, user, new HashSet<>(), Collections.singleton(movie), ENABLE);

        em.flush();

        // Post conditions
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Post.TABLE_NAME));
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testFindPostById() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        final long postId = helper.insertPost(TITLE, USER1_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        // Exercise
        final Optional<Post> post = postDao.findPostById(postId);

        // Post conditions
        Assert.assertTrue(post.isPresent());
        Assert.assertEquals(postId, post.get().getId());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testFindPostByIdOfDisabledPost() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        final long postId = helper.insertPost(TITLE, USER1_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);

        // Exercise
        final Optional<Post> post = postDao.findPostById(postId);

        // Post conditions
        Assert.assertFalse(post.isPresent());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:movies.sql")
    public void testFindPostsByMovie() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        final long post1 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPostMovie(post1, MOVIE1_ID);
        final long post2 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPostMovie(post2, MOVIE2_ID);
        final long post3 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPostMovie(post3, MOVIE1_ID);
        final long post4 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPostMovie(post4, MOVIE2_ID);

        Movie movie = em.find(Movie.class, MOVIE1_ID);

        final PaginatedCollection<Post> posts = postDao.findPostsByMovie(movie, true, DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(2, posts.getTotalCount());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:user2.sql")
    @Sql("classpath:categories.sql")
    public void testFindPostsByUser() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPost(TITLE, USER2_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPost(TITLE, USER2_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        User user = em.find(User.class, USER1_ID);

        final PaginatedCollection<Post> posts = postDao.findPostsByUser(user, true, DEFAULT_SORT_CRITERIA, 0, 2);

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

        final long post1 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post2 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post3 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post4 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(true, PostDao.SortCriteria.NEWEST, 1, 2);

        Assert.assertEquals(4, posts.getTotalCount());
        Assert.assertArrayEquals(new Long[]{post2, post4}, posts.getResults().stream().map(Post::getId).toArray());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testGetAllPostsOldest() {

        //Requires users with ID 1, 2 and 3.
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        final long post1 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post2 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post3 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post4 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(true, PostDao.SortCriteria.OLDEST, 1, 2);

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

        final long post1 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPostLike(post1, USER1_ID, UP_VOTE);
        helper.insertPostLike(post1, USER2_ID, UP_VOTE);
        final long post2 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPostLike(post2, USER1_ID, UP_VOTE);
        helper.insertPostLike(post2, USER2_ID, UP_VOTE);
        helper.insertPostLike(post2, USER3_ID, UP_VOTE);
        final long post3 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPostLike(post3, USER1_ID, UP_VOTE);
        helper.insertPostLike(post3, USER2_ID, DOWN_VOTE);
        helper.insertPostLike(post3, USER3_ID, DOWN_VOTE);
        final long post4 = helper.insertPost(TITLE, USER1_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(true, PostDao.SortCriteria.HOTTEST, 1, 2);

        Assert.assertEquals(4, posts.getTotalCount());
        Assert.assertArrayEquals(new Long[]{post4, post3}, posts.getResults().stream().map(Post::getId).toArray());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testGetAllPostsEmptyPage() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPost(TITLE, USER1_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(true, PostDao.SortCriteria.NEWEST, 10, 10);

        Assert.assertEquals(4, posts.getTotalCount());
        Assert.assertEquals(0, posts.getResults().size());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testGetAllPostsExcludingNotEnabled() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        helper.insertPost(TITLE, USER1_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.getAllPosts(true, PostDao.SortCriteria.NEWEST, 0, 2);

        Assert.assertEquals(2, posts.getTotalCount());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:user2.sql")
    @Sql("classpath:user3.sql")
    @Sql("classpath:categories.sql")
    public void testGetFollowedUsersPosts() {

        // Pre conditions
        User user1 = em.find(User.class, USER1_ID);
        User user2 = em.find(User.class, USER2_ID);
        User user3 = em.find(User.class, USER3_ID);

        long post1Id = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        long post2Id = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        long post3Id = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        long post4Id = helper.insertPost(TITLE, USER3_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        long post5Id = helper.insertPost(TITLE, USER2_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        long post6Id = helper.insertPost(TITLE, USER2_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        long post7Id = helper.insertPost(TITLE, USER2_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        long post8Id = helper.insertPost(TITLE, USER3_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        helper.insertFollowingUser(USER1_ID, USER3_ID);

        // Exercise
        PaginatedCollection<Post> posts = postDao.getFollowedUsersPosts(user3, true, PostDao.SortCriteria.NEWEST, 0, 10);

        // Follows only 1 and 1 has only 2 enabled posts
        Assert.assertEquals(2, posts.getTotalCount());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testGetUserFavouritePosts() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        User user = em.find(User.class, USER1_ID);

        long post1Id = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        long post2Id = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        long post3Id = helper.insertPost(TITLE, USER1_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, NOT_ENABLE);
        long post4Id = helper.insertPost(TITLE, USER1_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        helper.insertFavoritePost(post1Id, user.getId());
        helper.insertFavoritePost(post3Id, user.getId());

        final PaginatedCollection<Post> posts = postDao.getUserFavouritePosts(user, true, PostDao.SortCriteria.NEWEST, 0, 10);

        // Has 2 favs but 1 is not enabled
        Assert.assertEquals(1, posts.getTotalCount());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testSearchPosts() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        helper.insertPost("Title", USER1_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPost("Titulito", USER1_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPost("Tetle", USER1_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPost("Nombre", USER1_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.searchPosts("Tit", true, DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(2, posts.getTotalCount());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testSearchPostsByCategory() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        helper.insertPost("Title", USER1_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPost("Titulito", USER1_ID, CREATION_DATE.plusHours(2), CATEGORY_ID2, WORD_COUNT, BODY, ENABLE);
        helper.insertPost("Tetle", USER1_ID, CREATION_DATE.plusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        helper.insertPost("Nombre", USER1_ID, CREATION_DATE, CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.searchPostsByCategory("Tit", CATEGORY_NAME, true, DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(1, posts.getTotalCount());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testSearchPostsOlderThan() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        final long post1 = helper.insertPost("Title", USER1_ID, CREATION_DATE.minusHours(15), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post2 = helper.insertPost("Titulito", USER1_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post3 = helper.insertPost("Tetle", USER1_ID, CREATION_DATE.minusHours(5), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post4 = helper.insertPost("Nombre", USER1_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.searchPostsOlderThan("Tit", CREATION_DATE, true, DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(1, posts.getTotalCount());
        Assert.assertTrue(posts.getResults().stream().findFirst().isPresent());
        Assert.assertEquals(post2, posts.getResults().stream().findFirst().get().getId());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    public void testSearchPostsByCategoryAndOlderThan() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Post.TABLE_NAME);

        final long post1 = helper.insertPost("Title", USER1_ID, CREATION_DATE.minusHours(15), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post2 = helper.insertPost("Titulito", USER1_ID, CREATION_DATE.plusHours(2), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post3 = helper.insertPost("Nombre", USER1_ID, CREATION_DATE.plusHours(10), CATEGORY_ID, WORD_COUNT, BODY, ENABLE);
        final long post4 = helper.insertPost("Tittle", USER1_ID, CREATION_DATE.plusHours(10), CATEGORY_ID2, WORD_COUNT, BODY, ENABLE);

        final PaginatedCollection<Post> posts = postDao.searchPostsByCategoryAndOlderThan("Tit", CATEGORY_NAME,
                CREATION_DATE, true, DEFAULT_SORT_CRITERIA, 0, 2);

        Assert.assertEquals(1, posts.getTotalCount());
        Assert.assertTrue(posts.getResults().stream().findFirst().isPresent());
        Assert.assertEquals(post2, posts.getResults().stream().findFirst().get().getId());
    }

}