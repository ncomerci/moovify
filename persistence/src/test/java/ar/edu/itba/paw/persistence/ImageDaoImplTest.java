package ar.edu.itba.paw.persistence;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ImageDaoImplTest {

    private static final int BYTE_ARRAY_SIZE = 100;
    private static final String TAG = "Test";

    @Autowired
    private ImageDaoImpl imageDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert imageInsert;

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.imageInsert = new SimpleJdbcInsert(ds)
                .withTableName(TableNames.IMAGES.getTableName())
                .usingGeneratedKeyColumns("image_id");

    }

    @Rollback
    @Test
    public void testUploadImage() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, TableNames.IMAGES.getTableName());

        imageDao.uploadImage(new byte[BYTE_ARRAY_SIZE], TAG);

        final int count = JdbcTestUtils.countRowsInTable(jdbcTemplate, TableNames.IMAGES.getTableName());

        Assert.assertEquals(1, count);
    }
}