import Config.TestConfig;
import ar.edu.itba.paw.models.PostCategory;
import ar.edu.itba.paw.persistence.PostCategoryDaoImpl;
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

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.PostCategoryDaoImpl.POST_CATEGORY_ROW_MAPPER;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PostCategoryDaoImplTest {

    @Autowired
    private PostCategoryDaoImpl postCategoryDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TableNames.POST_CATEGORY.getTableName())
                .usingGeneratedKeyColumns("category_id");
    }

    @Test
    @Sql("classpath:test_inserts.sql")
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testFindById() {
        //        2. ejercitar
        final Optional<PostCategory> byId = postCategoryDao.findById(1);

//        3. post-condiciones
        Assert.assertTrue(byId.isPresent());
        Assert.assertEquals(1, byId.get().getId());
    }

    @Test
    @Sql("classpath:test_inserts.sql")
    @Sql(scripts = "classpath:clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetAllPostCategories() {
//        1. Precondiciones
        final List<PostCategory> postCategories = jdbcTemplate.query("SELECT * FROM post_category", POST_CATEGORY_ROW_MAPPER);
//        2. ejercitar
        final Collection<PostCategory> allPostCategories = postCategoryDao.getAllPostCategories();

//        3. post-condiciones
        Assert.assertNotNull(allPostCategories);
        Assert.assertEquals(postCategories.size(), allPostCategories.size());
    }


}
