package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
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
import java.util.Optional;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CommentDaoImplTest {

    private static final long POST1_ID = InsertHelper.POST1_ID;
    private static final long POST2_ID = InsertHelper.POST2_ID;
    private static final long POST3_ID = InsertHelper.POST3_ID;

    private static final long USER1_ID = InsertHelper.POST1_ID;
    private static final long USER2_ID = InsertHelper.POST2_ID;
    private static final long USER3_ID = InsertHelper.POST3_ID;

    private static final int CHILDREN_COUNT = 5;

    private static final CommentDao.SortCriteria NEWEST = CommentDao.SortCriteria.NEWEST;

    @Autowired
    private CommentDaoImpl commentDao;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    private InsertHelper helper;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.helper = new InsertHelper(jdbcTemplate);
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:post1.sql")
    public void testRegister() {

        // Pre conditions
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, Comment.TABLE_NAME, "user_id = ?", USER1_ID);

        Post post = em.find(Post.class, POST1_ID);
        User user = em.find(User.class, USER1_ID);

//        2. ejercitar
        commentDao.register(post, null, "body text", user, true);

        em.flush();

//        3. post-condiciones
        final String whereClause = "user_id = " + USER1_ID + " AND POST_ID = " + POST1_ID;
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, Comment.TABLE_NAME, whereClause)
        );
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:post1.sql")
    public void testFindCommentById() {

        // Pre conditions
        long id = helper.insertComment(true, null, POST1_ID, USER1_ID,"body");

//        2. ejercitar
        final Optional<Comment> comment = commentDao.findCommentById(id);

//        3. post-condiciones
        Assert.assertTrue(comment.isPresent());
        Assert.assertEquals(comment.get().getId(), id);
        Assert.assertEquals(comment.get().getPost().getId(), POST1_ID);
        Assert.assertEquals(comment.get().getUser().getId(), USER1_ID);
        final String whereClause = String.format("comment_id = %d AND user_id = %d AND post_id = %d", id, USER1_ID, POST1_ID);

        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, Comment.TABLE_NAME, whereClause)
        );
    }

    @Test
    public void testFindCommentByNonExistingId() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Comment.TABLE_NAME);

        final Optional<Comment> comment = commentDao.findCommentById(1);

        Assert.assertFalse(comment.isPresent());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:user2.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:post1.sql")
    public void testFindCommentChildrenByNewest() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Comment.TABLE_NAME);

        long parentCommentId = helper.insertComment(true, null, POST1_ID, USER1_ID, "body Text");
        long child1Id = helper.insertComment(true, parentCommentId, POST1_ID, USER1_ID, "body Text");
        long child2Id = helper.insertComment(true, parentCommentId, POST1_ID, USER2_ID, "body Text");
        long child3Id = helper.insertComment(true, parentCommentId, POST1_ID, USER1_ID, "body Text");
        long child4Id = helper.insertComment(true, parentCommentId, POST1_ID, USER2_ID, "body Text");
        long rootLevelCommentId = helper.insertComment(true, null, POST1_ID, USER2_ID, "body Text");

        Comment parentComment = em.find(Comment.class, parentCommentId);

        // Exercise
        final PaginatedCollection<Comment> commentChildren = commentDao
                .findCommentChildren(parentComment, true, CommentDao.SortCriteria.NEWEST , 1, 2);

        // Post conditions
        Assert.assertArrayEquals(new Long[]{child2Id, child1Id}, commentChildren.getResults().stream().map(Comment::getId).toArray());
        Assert.assertEquals(4, commentChildren.getTotalCount());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:user2.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:post1.sql")
    public void testFindCommentChildrenByOldest() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Comment.TABLE_NAME);

        long parentCommentId = helper.insertComment(true, null, POST1_ID, USER1_ID, "body Text");
        long child1Id = helper.insertComment(true, parentCommentId, POST1_ID, USER1_ID, "body Text");
        long child2Id = helper.insertComment(true, parentCommentId, POST1_ID, USER2_ID, "body Text");
        long child3Id = helper.insertComment(true, parentCommentId, POST1_ID, USER1_ID, "body Text");
        long child4Id = helper.insertComment(true, parentCommentId, POST1_ID, USER2_ID, "body Text");
        long rootLevelCommentId = helper.insertComment(true, null, POST1_ID, USER2_ID, "body Text");

        Comment parentComment = em.find(Comment.class, parentCommentId);

        // Exercise
        final PaginatedCollection<Comment> commentChildren = commentDao
                .findCommentChildren(parentComment, true, CommentDao.SortCriteria.OLDEST , 1, 2);

        // Post conditions
        Assert.assertArrayEquals(new Long[]{child3Id, child4Id}, commentChildren.getResults().stream().map(Comment::getId).toArray());
        Assert.assertEquals(4, commentChildren.getTotalCount());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:user2.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:post1.sql")
    public void testFindCommentChildrenByHottest() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Comment.TABLE_NAME);

        long parentCommentId = helper.insertComment(true, null, POST1_ID, USER1_ID, "body Text");
        long child1Id = helper.insertComment(true, parentCommentId, POST1_ID, USER1_ID, "body Text");
        helper.insertCommentLike(-1, child1Id, USER2_ID);
        helper.insertCommentLike(1, child1Id, USER1_ID);
        long child2Id = helper.insertComment(true, parentCommentId, POST1_ID, USER2_ID, "body Text");
        helper.insertCommentLike(1, child2Id, USER2_ID);
        helper.insertCommentLike(1, child2Id, USER1_ID);
        long child3Id = helper.insertComment(true, parentCommentId, POST1_ID, USER1_ID, "body Text");
        helper.insertCommentLike(-1, child3Id, USER2_ID);
        helper.insertCommentLike(-1, child3Id, USER1_ID);
        long child4Id = helper.insertComment(true, parentCommentId, POST1_ID, USER2_ID, "body Text");
        helper.insertCommentLike(1, child4Id, USER1_ID);
        long rootLevelCommentId = helper.insertComment(true, null, POST1_ID, USER2_ID, "body Text");

        Comment parentComment = em.find(Comment.class, parentCommentId);

        // Exercise
        final PaginatedCollection<Comment> commentChildren = commentDao
                .findCommentChildren(parentComment, true, CommentDao.SortCriteria.HOTTEST , 1, 2);

        // Post conditions
        Assert.assertArrayEquals(new Long[]{child1Id, child3Id}, commentChildren.getResults().stream().map(Comment::getId).toArray());
        Assert.assertEquals(4, commentChildren.getTotalCount());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:user2.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:post1.sql")
    @Sql("classpath:post2.sql")
    @Sql("classpath:post3.sql")
    public void testFindCommentsByPost() {

        // Pre conditions
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, Comment.TABLE_NAME,
                "post_id = ?", POST1_ID);

        long comment1ID = helper.insertComment(true, null, POST1_ID, USER1_ID, "body Text");
        long comment2ID = helper.insertComment(true, null, POST2_ID, USER2_ID, "body Text");
        long comment3ID = helper.insertComment(true, null, POST1_ID, USER1_ID, "body Text");
        long comment4ID = helper.insertComment(true, null, POST1_ID, USER2_ID, "body Text");
        long comment5ID = helper.insertComment(true, null, POST3_ID, USER2_ID, "body Text");
        long comment6ID = helper.insertComment(true, null, POST1_ID, USER2_ID, "body Text");

        Post post = em.find(Post.class, POST1_ID);

//        2. ejercitar
        final PaginatedCollection<Comment> commentsByPost = commentDao.findCommentsByPost(post, true, NEWEST, 1, 2);

//        3. post-condiciones
        Assert.assertArrayEquals(new Long[]{comment3ID, comment1ID}, commentsByPost.getResults().stream().map(Comment::getId).toArray());
        Assert.assertEquals(4, commentsByPost.getTotalCount());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:user2.sql")
    @Sql("classpath:user3.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:post1.sql")
    @Sql("classpath:post2.sql")
    @Sql("classpath:post3.sql")
    public void testFindCommentsByUser() {

        // Pre conditions
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, Comment.TABLE_NAME,
                "user_id = ?", USER1_ID);

        long comment1ID = helper.insertComment(true, null, POST1_ID, USER1_ID, "body Text");
        long comment2ID = helper.insertComment(true, null, POST2_ID, USER2_ID, "body Text");
        long comment3ID = helper.insertComment(true, null, POST1_ID, USER2_ID, "body Text");
        long comment4ID = helper.insertComment(true, null, POST1_ID, USER2_ID, "body Text");
        long comment5ID = helper.insertComment(true, null, POST3_ID, USER3_ID, "body Text");
        long comment6ID = helper.insertComment(true, null, POST1_ID, USER2_ID, "body Text");

        User user = em.find(User.class, USER2_ID);

//        2. ejercitar
        final PaginatedCollection<Comment> commentsByUser = commentDao.findCommentsByUser(user, true, NEWEST, 1, 2);

//        3. post-condiciones
        Assert.assertArrayEquals(new Long[]{comment3ID, comment2ID}, commentsByUser.getResults().stream().map(Comment::getId).toArray());
        Assert.assertEquals(4, commentsByUser.getTotalCount());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:user2.sql")
    @Sql("classpath:user3.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:post1.sql")
    @Sql("classpath:post2.sql")
    @Sql("classpath:post3.sql")
    public void testGetDeletedComments() {

        // Pre conditions
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, Comment.TABLE_NAME,
                "enabled = false");

        long comment1ID = helper.insertComment(true, null, POST1_ID, USER1_ID, "body Text");
        long comment2ID = helper.insertComment(false, null, POST2_ID, USER2_ID, "body Text");
        long comment3ID = helper.insertComment(false, null, POST1_ID, USER1_ID, "body Text");
        long comment4ID = helper.insertComment(true, null, POST1_ID, USER2_ID, "body Text");
        long comment5ID = helper.insertComment(false, null, POST3_ID, USER3_ID, "body Text");
        long comment6ID = helper.insertComment(false, null, POST1_ID, USER2_ID, "body Text");

//        2. ejercitar
        final PaginatedCollection<Comment> deletedComments = commentDao.getAllComments(false, NEWEST, 1, 2);

//        3. post-condiciones
        Assert.assertArrayEquals(new Long[]{comment3ID, comment2ID}, deletedComments.getResults().stream().map(Comment::getId).toArray());
        Assert.assertEquals(4, deletedComments.getTotalCount());
    }
}
