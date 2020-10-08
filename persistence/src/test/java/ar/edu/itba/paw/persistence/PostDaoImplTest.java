package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategory;
import ar.edu.itba.paw.models.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PostDaoImplTest {

    private static final String TITLE = "Title";
    private static final String BODY = "Body";
    private static final int WORD_COUNT = 1;
    private static final PostCategory CATEGORY = Mockito.when(Mockito.mock(PostCategory.class).getId()).thenReturn(1L).getMock();
    private static final User USER = Mockito.when(Mockito.mock(User.class).getId()).thenReturn(1L).getMock();
    private static final Set<String> TAGS = Collections.singleton("Tag");
    private static final Set<Long> MOVIES = Collections.singleton(1L);
    private static final boolean ENABLE = true;

    @Autowired
    private PostDaoImpl postDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
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

    @Test
    public void findPostById() {


    }

    @Test
    public void findPostsByMovie() {
    }

    @Test
    public void findPostsByUser() {
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
}