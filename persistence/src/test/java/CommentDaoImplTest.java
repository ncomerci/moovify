import Config.TestConfig;
import ar.edu.itba.paw.persistence.CommentDaoImpl;
import ar.edu.itba.paw.persistence.TableNames;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CommentDaoImplTest {

    private static final long POST_ID = 1;
    private static final long USER_ID = 1;
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


    @Test
    @Sql("classpath:test_inserts.sql")
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testRegister() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(), "user_id = ?", USER_ID);

//        2. ejercitar
        commentDao.register(POST_ID, null, BODY, USER_ID);

//        3. post-condiciones
        final String whereClause = "user_id = " + USER_ID + " AND POST_ID = " + POST_ID;
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.COMMENTS.getTableName(), whereClause)
        );
    }

    @Test(expected = NullPointerException.class)
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testInvalidRegister() {
        commentDao.register(POST_ID, null, null, USER_ID);
    }
}
