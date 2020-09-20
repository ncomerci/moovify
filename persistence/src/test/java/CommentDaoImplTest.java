import Config.TestConfig;
import ar.edu.itba.paw.persistence.CommentDaoImpl;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CommentDaoImplTest {

    private static final long POST_ID = 1;
    private static final String EMAIL = "abc@test.com";
    private static final String EMAIL_COM = "'" + EMAIL + "'";
    private static final String BODY = "testing";

    @Autowired
    private CommentDaoImpl commentDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    // TODO: Rehacer test

//    @Test
//    @Sql("classpath:test_inserts.sql")
//    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    public void testRegister() {
////        1. precondiciones
//        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(), "user_email = ?", EMAIL);
//
////        2. ejercitar
//        final Comment comment = commentDao.register(POST_ID, null, BODY, EMAIL);
//
////        3. post-condiciones
//        Assert.assertNotNull(comment);
//        Assert.assertNull(comment.getParentId());
//        Assert.assertEquals(EMAIL, comment.getUserEmail());
//        Assert.assertEquals(BODY, comment.getBody());
//        Assert.assertEquals(POST_ID, comment.getPostId());
//        Assert.assertEquals(1,
//                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(), "user_email = " + EMAIL_COM)
//        );
//    }

    // TODO: Rehacer test

//    @Test(expected = NullPointerException.class)
//    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    public void testInvalidRegister() {
//        commentDao.register(POST_ID, null, BODY, null);
//    }
}
