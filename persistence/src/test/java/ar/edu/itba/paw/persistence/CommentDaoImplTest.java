package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.models.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CommentDaoImplTest {

    private static final long POST1_ID = 1L;
    private static final long POST2_ID = 2L;
    private static final long POST3_ID = 3L;

    private static final long USER1_ID = 1L;
    private static final long USER2_ID = 2L;
    private static final long USER3_ID = 3L;

    private static final CommentDao.SortCriteria NEWEST = CommentDao.SortCriteria.NEWEST;

    private static long commentIdCount = 0;
    private static long commentLikesIdCount = 0;

    @Autowired
    private CommentDaoImpl commentDao;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert likeInsert;
    private SimpleJdbcInsert commentInsert;

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);

        this.likeInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(CommentLike.TABLE_NAME);

        this.commentInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(Comment.TABLE_NAME);
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:post1.sql")
    public void testRegister() {
//        1. precondiciones
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
//        1. precondiciones
        long id = insertComment(true, null, POST1_ID, USER1_ID,"body");

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

        long parentCommentId = insertComment(true, null, POST1_ID, USER1_ID, "body Text");
        long child1Id = insertComment(true, parentCommentId, POST1_ID, USER1_ID, "body Text");
        long child2Id = insertComment(true, parentCommentId, POST1_ID, USER2_ID, "body Text");
        long child3Id = insertComment(true, parentCommentId, POST1_ID, USER1_ID, "body Text");
        long child4Id = insertComment(true, parentCommentId, POST1_ID, USER2_ID, "body Text");
        long rootLevelCommentId = insertComment(true, null, POST1_ID, USER2_ID, "body Text");

        Comment parentComment = em.find(Comment.class, parentCommentId);

        // Exercise
        final PaginatedCollection<Comment> commentChildren = commentDao
                .findCommentChildren(parentComment, CommentDao.SortCriteria.NEWEST , 1, 2);

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

        long parentCommentId = insertComment(true, null, POST1_ID, USER1_ID, "body Text");
        long child1Id = insertComment(true, parentCommentId, POST1_ID, USER1_ID, "body Text");
        long child2Id = insertComment(true, parentCommentId, POST1_ID, USER2_ID, "body Text");
        long child3Id = insertComment(true, parentCommentId, POST1_ID, USER1_ID, "body Text");
        long child4Id = insertComment(true, parentCommentId, POST1_ID, USER2_ID, "body Text");
        long rootLevelCommentId = insertComment(true, null, POST1_ID, USER2_ID, "body Text");

        Comment parentComment = em.find(Comment.class, parentCommentId);

        // Exercise
        final PaginatedCollection<Comment> commentChildren = commentDao
                .findCommentChildren(parentComment, CommentDao.SortCriteria.OLDEST , 1, 2);

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

        long parentCommentId = insertComment(true, null, POST1_ID, USER1_ID, "body Text");
        long child1Id = insertComment(true, parentCommentId, POST1_ID, USER1_ID, "body Text");
        insertCommentLike(-1, child1Id, USER2_ID);
        insertCommentLike(1, child1Id, USER1_ID);
        long child2Id = insertComment(true, parentCommentId, POST1_ID, USER2_ID, "body Text");
        insertCommentLike(1, child2Id, USER2_ID);
        insertCommentLike(1, child2Id, USER1_ID);
        long child3Id = insertComment(true, parentCommentId, POST1_ID, USER1_ID, "body Text");
        insertCommentLike(-1, child3Id, USER2_ID);
        insertCommentLike(-1, child3Id, USER1_ID);
        long child4Id = insertComment(true, parentCommentId, POST1_ID, USER2_ID, "body Text");
        insertCommentLike(1, child4Id, USER1_ID);
        long rootLevelCommentId = insertComment(true, null, POST1_ID, USER2_ID, "body Text");

        Comment parentComment = em.find(Comment.class, parentCommentId);

        // Exercise
        final PaginatedCollection<Comment> commentChildren = commentDao
                .findCommentChildren(parentComment, CommentDao.SortCriteria.HOTTEST , 1, 2);

        // Post conditions
        Assert.assertArrayEquals(new Long[]{child1Id, child3Id}, commentChildren.getResults().stream().map(Comment::getId).toArray());
        Assert.assertEquals(4, commentChildren.getTotalCount());
    }


    // Even though the recursive query in CommentDaoImpl is ANSI, hsqldb fails running it.
    // We decided to disable the tests involving that query for now.
    /*
    @Rollback
    @Test
    public void testFindCommentDescendantsByNewest() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, Comment.TABLE_NAME, "parent_id = ?", PARENT_ID);
        COMMENT_ROW.put("parent_id", PARENT_ID);
        final int cant = PAGE_SIZE*2;
        StringBuilder whereClause = new StringBuilder();
        Long[] ids = new Long[cant];

        for(int i = 0 ; i < cant ; i++) {
            COMMENT_ROW.put("creation_date", Timestamp.valueOf(LocalDateTime.of(2020, 10, 10, i, 0)));
            ids[i] = commentInsert.executeAndReturnKey(COMMENT_ROW).longValue();
            whereClause.append("comment_id = ").append(ids[i]).append(" OR ");
        }
        whereClause.setLength(whereClause.length() - " OR ".length());
        Comment comment = Mockito.when(Mockito.mock(Comment.class).getId()).thenReturn(PARENT_ID).getMock();

//        2. ejercitar
        final PaginatedCollection<Comment> commentDescendants1 = commentDao.findCommentDescendants(comment, NEWEST, PAGE_NUMBER, PAGE_SIZE);
        final PaginatedCollection<Comment> commentDescendants2 = commentDao.findCommentDescendants(comment, NEWEST, PAGE_NUMBER+1, PAGE_SIZE);

//        3. post-condiciones
        Assert.assertNotNull(commentDescendants1);
        Assert.assertNotNull(commentDescendants2);

        final Collection<Comment> descendants1Results = commentDescendants1.getResults();
        final Collection<Comment> descendants2Results = commentDescendants2.getResults();
        final Comment[] array1 = descendants1Results.toArray(new Comment[0]);
        final Comment[] array2 = descendants2Results.toArray(new Comment[0]);

        Assert.assertEquals(PAGE_SIZE, descendants1Results.size());
        Assert.assertEquals(PAGE_SIZE, descendants2Results.size());

        for(int i = 0 ; i < PAGE_SIZE ; i++) {
            Assert.assertEquals(ids[cant - 1 - i], (Long) array1[i].getId());
            Assert.assertEquals(ids[cant/2 - 1 - i], (Long) array2[i].getId());

        }
        Assert.assertEquals(cant,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, Comment.TABLE_NAME, whereClause.toString())
        );
    }
    */


    // Even though the recursive query in CommentDaoImpl is ANSI, hsqldb fails running it.
    // We decided to disable the tests involving that query for now.
    /*
    @Rollback
    @Test
    public void testFindCommentDescendantsByHottest() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, Comment.TABLE_NAME, "parent_id = ?", PARENT_ID);
        final int cant = PAGE_SIZE*2;
        COMMENT_ROW.put("parent_id", PARENT_ID);

        StringBuilder whereClause = new StringBuilder();
        Long[] ids = new Long[cant];
        Long[] userIds = new Long[cant];
        Map<String, Object> like_row = new HashMap<>();
        like_row.put("value", 1);

        int i, j;
        for(i = 0 ; i < cant ; i++) {
            COMMENT_ROW.put("creation_date", Timestamp.valueOf(LocalDateTime.now()));
            ids[i] = commentInsert.executeAndReturnKey(COMMENT_ROW).longValue();
            userIds[i] = insertUser(Timestamp.valueOf(LocalDateTime.now()), String.valueOf(i), "", "", String.valueOf(i), "", true);
            for(j = 0; j <= i ; j++) {
                like_row.put("user_id", userIds[j]);
                like_row.put("comment_id", ids[i]);
                likeInsert.execute(like_row);
            }
            whereClause.append("comment_id = ").append(ids[i]).append(" OR ");
        }
        whereClause.setLength(whereClause.length() - " OR ".length());
        Comment comment = Mockito.when(Mockito.mock(Comment.class).getId()).thenReturn(PARENT_ID).getMock();

//        2. ejercitar
        final PaginatedCollection<Comment> commentDescendants1 = commentDao.findCommentDescendants(comment, CommentDao.SortCriteria.HOTTEST, PAGE_NUMBER, PAGE_SIZE);
        final PaginatedCollection<Comment> commentDescendants2 = commentDao.findCommentDescendants(comment, CommentDao.SortCriteria.HOTTEST, PAGE_NUMBER+1, PAGE_SIZE);

//        3. post-condiciones
        Assert.assertNotNull(commentDescendants1);
        Assert.assertNotNull(commentDescendants2);

        final Collection<Comment> descendants1Results = commentDescendants1.getResults();
        final Collection<Comment> descendants2Results = commentDescendants2.getResults();
        final Comment[] array1 = descendants1Results.toArray(new Comment[0]);
        final Comment[] array2 = descendants2Results.toArray(new Comment[0]);

        Assert.assertEquals(PAGE_SIZE, descendants1Results.size());
        Assert.assertEquals(PAGE_SIZE, descendants2Results.size());

        for(i = 0 ; i < PAGE_SIZE ; i++) {
            Assert.assertEquals(ids[cant - 1 - i], (Long) array1[i].getId());
            Assert.assertEquals(ids[cant/2 - 1 - i], (Long) array2[i].getId());

        }
        Assert.assertEquals(cant,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, Comment.TABLE_NAME, whereClause.toString())
        );
    }
    */

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:user2.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:post1.sql")
    @Sql("classpath:post2.sql")
    @Sql("classpath:post3.sql")
    public void testFindCommentsByPost() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, Comment.TABLE_NAME,
                "post_id = ?", POST1_ID);

        long comment1ID = insertComment(true, null, POST1_ID, USER1_ID, "body Text");
        long comment2ID = insertComment(true, null, POST2_ID, USER2_ID, "body Text");
        long comment3ID = insertComment(true, null, POST1_ID, USER1_ID, "body Text");
        long comment4ID = insertComment(true, null, POST1_ID, USER2_ID, "body Text");
        long comment5ID = insertComment(true, null, POST3_ID, USER2_ID, "body Text");
        long comment6ID = insertComment(true, null, POST1_ID, USER2_ID, "body Text");

        Post post = em.find(Post.class, POST1_ID);

//        2. ejercitar
        final PaginatedCollection<Comment> commentsByPost = commentDao.findCommentsByPost(post, NEWEST, 1, 2);

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
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, Comment.TABLE_NAME,
                "user_id = ?", USER1_ID);

        long comment1ID = insertComment(true, null, POST1_ID, USER1_ID, "body Text");
        long comment2ID = insertComment(true, null, POST2_ID, USER2_ID, "body Text");
        long comment3ID = insertComment(true, null, POST1_ID, USER2_ID, "body Text");
        long comment4ID = insertComment(true, null, POST1_ID, USER2_ID, "body Text");
        long comment5ID = insertComment(true, null, POST3_ID, USER3_ID, "body Text");
        long comment6ID = insertComment(true, null, POST1_ID, USER2_ID, "body Text");

        User user = em.find(User.class, USER2_ID);

//        2. ejercitar
        final PaginatedCollection<Comment> commentsByUser = commentDao.findCommentsByUser(user, NEWEST, 1, 2);

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
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, Comment.TABLE_NAME,
                "enabled = false");

        long comment1ID = insertComment(true, null, POST1_ID, USER1_ID, "body Text");
        long comment2ID = insertComment(false, null, POST2_ID, USER2_ID, "body Text");
        long comment3ID = insertComment(false, null, POST1_ID, USER1_ID, "body Text");
        long comment4ID = insertComment(true, null, POST1_ID, USER2_ID, "body Text");
        long comment5ID = insertComment(false, null, POST3_ID, USER3_ID, "body Text");
        long comment6ID = insertComment(false, null, POST1_ID, USER2_ID, "body Text");

//        2. ejercitar
        final PaginatedCollection<Comment> deletedComments = commentDao.getDeletedComments(NEWEST, 1, 2);

//        3. post-condiciones
        Assert.assertArrayEquals(new Long[]{comment3ID, comment2ID}, deletedComments.getResults().stream().map(Comment::getId).toArray());
        Assert.assertEquals(4, deletedComments.getTotalCount());
    }

    @Test
    @Sql("classpath:user1.sql")
    @Sql("classpath:user2.sql")
    @Sql("classpath:user3.sql")
    @Sql("classpath:categories.sql")
    @Sql("classpath:post1.sql")
    @Sql("classpath:post2.sql")
    @Sql("classpath:post3.sql")
    public void testSearchDeletedComments() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Comment.TABLE_NAME);

        long comment1ID = insertComment(false, null, POST1_ID, USER1_ID, "body Text");
        long comment2ID = insertComment(false, null, POST2_ID, USER2_ID, "alterante message");
        long comment3ID = insertComment(false, null, POST1_ID, USER1_ID, "Text");
        long comment4ID = insertComment(false, null, POST1_ID, USER2_ID, "body");
        long comment5ID = insertComment(false, null, POST3_ID, USER3_ID, "bodyText");
        long comment6ID = insertComment(false, null, POST1_ID, USER2_ID, "BodYBodYBodY");
        long comment7ID = insertComment(true, null, POST1_ID, USER2_ID, "bODy");

//        2. ejercitar
        final PaginatedCollection<Comment> deletedComments = commentDao.searchDeletedComments("bODy", NEWEST, 1, 2);

//        3. post-condiciones
        Assert.assertArrayEquals(new Long[]{comment4ID, comment1ID}, deletedComments.getResults().stream().map(Comment::getId).toArray());
        Assert.assertEquals(4, deletedComments.getTotalCount());
    }

    private long insertComment(boolean enabled, Long parentId, long postId, long userId, String body){

        long id = ++commentIdCount;

        Map<String, Object> comment = new HashMap<>();

        comment.put("comment_id", id);
        comment.put("body", body);
        comment.put("creation_date", Timestamp.valueOf(LocalDateTime.of(2020, 8, 6, 12, 14).plusHours(id)));
        comment.put("edited", false);
        comment.put("enabled", enabled);
        comment.put("last_edited", null);
        comment.put("parent_id", parentId);
        comment.put("post_id", postId);
        comment.put("user_id", userId);

        commentInsert.execute(comment);

        return id;
    }

    private void insertCommentLike(int value, long commentId, long userId){

        long id = ++commentLikesIdCount;

        Map<String, Object> map = new HashMap<>();

        map.put("comments_likes_id", id);
        map.put("value", value);
        map.put("user_id", userId);
        map.put("comment_id", commentId);
        likeInsert.execute(map);
    }
}
