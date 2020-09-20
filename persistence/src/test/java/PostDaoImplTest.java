import Config.TestConfig;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.persistence.PostDaoImpl;
import ar.edu.itba.paw.persistence.TableNames;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PostDaoImplTest {

    private static final long CATEGORY_ID = 1;
    private static final String TITLE = "POST TEST";
    private static final String EMAIL = "abc@test.com";
    private static final String BODY = "testing";
    private static final Set<Long> MOVIES = new HashSet<>(Collections.singletonList(1L));


    @Autowired
    private PostDaoImpl postDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TableNames.POSTS.getTableName())
                .usingGeneratedKeyColumns("post_id");
    }

    @Test
    @Sql("classpath:test_inserts.sql")
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testRegister() {
//        1. precondiciones
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TableNames.POSTS.getTableName(), "email = ?", EMAIL);

//        2. ejercitar
        final long post_id = postDao.register(TITLE, EMAIL, BODY, CATEGORY_ID, null, MOVIES);

//        3. post-condiciones
        final String whereClause = "post_id = " + post_id + " AND title = " + "'" + TITLE + "'" + " AND email = " + "'" + EMAIL + "'" +
                " AND body = " + "'" + BODY + "'" + " AND category_id = " + CATEGORY_ID ;
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POSTS.getTableName(), whereClause)
        );
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidRegister() {
//        2. ejercitar
        postDao.register(null, null, null, CATEGORY_ID, null, null);
    }

    @Test
    @Sql("classpath:test_inserts.sql")
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testFindPostById() {
//        1. precondiciones
        Map<String, Object> map = new HashMap<String, Object>() {{
           put("creation_date", Timestamp.valueOf(LocalDateTime.now()));
           put("title", TITLE);
           put("email", EMAIL);
           put("category_id", CATEGORY_ID);
           put("word_count", 1);
           put("body", BODY);
        }};
        final Number key = jdbcInsert.executeAndReturnKey(map);

//        2. ejercitar
        final Optional<Post> postById = postDao.findPostById(key.longValue(), EnumSet.noneOf(PostDao.FetchRelation.class));

//        3. post-condiciones
        Assert.assertTrue(postById.isPresent());
        Assert.assertEquals(key.longValue(), postById.get().getId());
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POSTS.getTableName(), "post_id = " + postById.get().getId())
        );
    }

    @Test
    @Sql("classpath:test_inserts.sql")
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testFindPostsByMovieId() {
//        1. precondiciones
        final long movie_id = 8;

//        2. ejercitar
        final Collection<Post> postsByMovieId = postDao.findPostsByMovieId(movie_id, EnumSet.noneOf(PostDao.FetchRelation.class));

//        3. post-condiciones
        Assert.assertNotNull(postsByMovieId);
        Assert.assertEquals(
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, TableNames.POST_MOVIE.getTableName(), "movie_id = " + movie_id),
                postsByMovieId.size()
        );
    }

    @Test
    @Sql("classpath:test_inserts.sql")
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetAllPostsOrderByNewest() {
//        1. precondiciones

//        2. ejercitar
        final Collection<Post> allPosts = postDao.getAllPosts(EnumSet.noneOf(PostDao.FetchRelation.class), PostDao.SortCriteria.NEWEST);

//        3. post-condiciones
        Assert.assertNotNull(allPosts);
        Assert.assertEquals(
                JdbcTestUtils.countRowsInTable(jdbcTemplate, TableNames.POSTS.getTableName()),
                allPosts.size()
        );

    }
}
