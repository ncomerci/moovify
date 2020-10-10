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

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CommentDaoImplTest {

    private static final long POST_ID = 1L;
    private static final Post POST = Mockito.when(Mockito.mock(Post.class).getId()).thenReturn(POST_ID).getMock();
    private static final long USER_ID = 1L;
    private static final User USER = Mockito.when(Mockito.mock(User.class).getId()).thenReturn(USER_ID).getMock();
    private static final long COMMENT_ID = 1L;
    private static final Comment COMMENT = Mockito.when(Mockito.mock(Comment.class).getId()).thenReturn(COMMENT_ID).getMock();
    private static final String BODY = "testing";
    private static final CommentDao.SortCriteria NEWEST = CommentDao.SortCriteria.NEWEST;
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 5;
    private static final Map<String, Object> COMMENT_ROW = new HashMap<>();

    @Autowired
    private CommentDaoImpl commentDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert likeJdbcInsert;
    private SimpleJdbcInsert commentInsert;

    private void mapInitializer() {
        COMMENT_ROW.put("post_id", POST_ID);
        COMMENT_ROW.put("user_id", USER_ID);
        COMMENT_ROW.put("creation_date", Timestamp.valueOf(LocalDateTime.now()));
        COMMENT_ROW.put("body", BODY);
        COMMENT_ROW.put("enabled", true);
    }

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.likeJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TableNames.COMMENTS_LIKES.getTableName());
        this.commentInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TableNames.COMMENTS.getTableName())
                .usingGeneratedKeyColumns("comment_id");
        mapInitializer();
    }


    @Rollback
    @Test
    public void testRegister() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(), "user_id = ?", USER_ID);

//        2. ejercitar
        commentDao.register(POST, null, BODY, USER, true);

//        3. post-condiciones
        final String whereClause = "user_id = " + USER_ID + " AND POST_ID = " + POST_ID;
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(), whereClause)
        );
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidRegister() {
        commentDao.register(null, null, null, null, true);
    }

    @Rollback
    @Test
    public void testLikeComment() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.COMMENTS_LIKES.getTableName(),
                "user_id = ? AND comment_id = ?", USER_ID, COMMENT_ID);
        final int value = 1;

//        2. ejercitar
        commentDao.likeComment(COMMENT, USER, value);

//        3. post-condiciones
        final String whereClause = String.format("user_id = %d AND comment_id = %d AND value = %d", USER_ID, COMMENT_ID, value);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.COMMENTS_LIKES.getTableName(), whereClause)
        );
    }

    @Rollback
    @Test
    public void removeLike() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.COMMENTS_LIKES.getTableName(),
                "user_id = ? AND comment_id = ?", USER_ID, COMMENT_ID);
        final long value = 1L;
        Map<String, Object> row = new HashMap<>();
        row.put("COMMENT_ID", COMMENT_ID);
        row.put("user_id", USER_ID);
        row.put("value", value);
        likeJdbcInsert.execute(row);
        Comment comment = Mockito.when(Mockito.mock(Comment.class).getId()).thenReturn(COMMENT_ID).getMock();

//        2. ejercitar
        commentDao.removeLike(comment, USER);

//        3. post-condiciones
        final String whereClause = String.format("user_id = %d AND comment_id = %d", USER_ID, COMMENT_ID);
        Assert.assertEquals(0,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.COMMENTS_LIKES.getTableName(), whereClause)
        );
    }

    @Rollback
    @Test
    public void testDeleteComment() {
//        1. precondiciones
        long id = commentInsert.executeAndReturnKey(COMMENT_ROW).longValue();
        Comment comment = Mockito.when(Mockito.mock(Comment.class).getId()).thenReturn(id).getMock();

//        2. ejercitar
        commentDao.deleteComment(comment);

//        3. post-condiciones
        final String whereClause = String.format("comment_id = %d AND enabled = false", id);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(), whereClause)
        );
    }

    @Rollback
    @Test
    public void testRestoreComment() {
//        1. precondiciones
        long id = commentInsert.executeAndReturnKey(COMMENT_ROW).longValue();
        Comment comment = Mockito.when(Mockito.mock(Comment.class).getId()).thenReturn(id).getMock();

//        2. ejercitar
        commentDao.restoreComment(comment);

//        3. post-condiciones
        final String whereClause = String.format("comment_id = %d AND enabled = true", id);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(), whereClause)
        );
    }

    @Rollback
    @Test
    public void testFindCommentById() {
//        1. precondiciones
        long id = commentInsert.executeAndReturnKey(COMMENT_ROW).longValue();

//        2. ejercitar
        final Optional<Comment> comment = commentDao.findCommentById(id);

//        3. post-condiciones
        Assert.assertTrue(comment.isPresent());
        Assert.assertEquals(comment.get().getId(), id);
        Assert.assertEquals(comment.get().getPost().getId(), POST_ID);
        Assert.assertEquals(comment.get().getUser().getId(), USER_ID);
        final String whereClause = String.format("comment_id = %d AND user_id = %d AND post_id = %d", id, USER_ID, POST_ID);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(), whereClause)
        );
    }

    @Rollback
    @Test
    public void testFindCommentDescendants() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(),
                "user_id = ? AND post_id = ?", USER_ID, POST_ID);
        COMMENT_ROW.put("parent_id", 2);
        long id = commentInsert.executeAndReturnKey(COMMENT_ROW).longValue();
        COMMENT_ROW.put("parent_id", null);
        Comment comment = Mockito.when(Mockito.mock(Comment.class).getId()).thenReturn(2L).getMock();

//        2. ejercitar
        final PaginatedCollection<Comment> commentDescendants = commentDao.findCommentDescendants(comment, NEWEST, PAGE_NUMBER, PAGE_SIZE);

//        3. post-condiciones
        Assert.assertNotNull(commentDescendants);
        Assert.assertEquals(1, commentDescendants.getTotalCount());
        final String whereClause = String.format("comment_id = %d AND user_id = %d AND post_id = %d", id, USER_ID, POST_ID);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(), whereClause)
        );
    }

    @Rollback
    @Test
    public void testFindCommentsByPost() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(),
                "post_id = ?", POST_ID);
        long id = commentInsert.executeAndReturnKey(COMMENT_ROW).longValue();

//        2. ejercitar
        final PaginatedCollection<Comment> commentsByPost = commentDao.findCommentsByPost(POST, NEWEST, PAGE_NUMBER, PAGE_SIZE);

//        3. post-condiciones
        Assert.assertNotNull(commentsByPost);
        Assert.assertEquals(id, commentsByPost.getResults().toArray(new Comment[0])[0].getId());
        final String whereClause = String.format("comment_id = %d AND post_id = %d", id, POST_ID);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(), whereClause)
        );
    }

    @Rollback
    @Test
    public void testFindCommentsByUser() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(),
                "user_id = ?", USER_ID);
        long id = commentInsert.executeAndReturnKey(COMMENT_ROW).longValue();

//        2. ejercitar
        final PaginatedCollection<Comment> commentsByUser = commentDao.findCommentsByUser(USER, NEWEST, PAGE_NUMBER, PAGE_SIZE);

//        3. post-condiciones
        Assert.assertNotNull(commentsByUser);
        Assert.assertEquals(id, commentsByUser.getResults().toArray(new Comment[0])[0].getId());
        final String whereClause = String.format("comment_id = %d AND user_id = %d", id, USER_ID);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(), whereClause)
        );
    }

    @Rollback
    @Test
    public void testGetDeletedComments() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(),
                "enabled = false");
        COMMENT_ROW.put("enabled", false);
        long id = commentInsert.executeAndReturnKey(COMMENT_ROW).longValue();

//        2. ejercitar
        final PaginatedCollection<Comment> deletedComments = commentDao.getDeletedComments(NEWEST, PAGE_NUMBER, PAGE_SIZE);

//        3. post-condiciones
        Assert.assertNotNull(deletedComments);
        Assert.assertEquals(id, deletedComments.getResults().toArray(new Comment[0])[0].getId());
        final String whereClause = String.format("comment_id = %d AND user_id = %d AND post_id = %d  AND enabled = false", id, USER_ID, POST_ID);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(), whereClause)
        );
    }

    @Rollback
    @Test
    public void testSearchDeletedComments() {
        //        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(),
                "body = ?", BODY);
        COMMENT_ROW.put("enabled", false);
        long id = commentInsert.executeAndReturnKey(COMMENT_ROW).longValue();

//        2. ejercitar
        final PaginatedCollection<Comment> deletedComments = commentDao.searchDeletedComments(BODY, NEWEST, PAGE_NUMBER, PAGE_SIZE);

//        3. post-condiciones
        Assert.assertNotNull(deletedComments);
        Assert.assertEquals(id, deletedComments.getResults().toArray(new Comment[0])[0].getId());
        final String whereClause = String.format("comment_id = %d AND body = '%s' AND enabled = false", id, BODY);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(), whereClause)
        );
    }
}
