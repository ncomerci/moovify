package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Rollback
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ImageDaoImplTest {

    private static final int BYTE_ARRAY_SIZE = 100;
    private static final int IMG_ID = 6;
    private static final byte[] IMG_DATA = new byte[BYTE_ARRAY_SIZE];

    @Autowired
    private ImageDaoImpl imageDao;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert imageInsert;

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.imageInsert = new SimpleJdbcInsert(ds)
                .withTableName(Image.TABLE_NAME);

    }

    @Test
    public void testUploadImage() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Image.TABLE_NAME);

        // Exercise
        imageDao.uploadImage(new byte[BYTE_ARRAY_SIZE], Image.DEFAULT_TYPE);

        em.flush();

        final int count = JdbcTestUtils.countRowsInTable(jdbcTemplate, Image.TABLE_NAME);

        // Post conditions
        Assert.assertEquals(1, count);
    }

    @Test
    public void testFindImageById() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Image.TABLE_NAME);

        Map<String, Object> imgMap = new HashMap<>();
        imgMap.put("image_id", IMG_ID);
        imgMap.put("image", IMG_DATA);

        imageInsert.execute(imgMap);

        // Exercise
        Optional<Image> img = imageDao.findImageById(IMG_ID);

        // Post conditions
        Assert.assertTrue(img.isPresent());
        Assert.assertEquals(IMG_ID, img.get().getId());
        Assert.assertArrayEquals(IMG_DATA, img.get().getData());
    }

    @Test
    public void testFindImageByNotPresentId() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Image.TABLE_NAME);

        // Exercise
        Optional<Image> img = imageDao.findImageById(IMG_ID);

        // Post conditions
        Assert.assertFalse(img.isPresent());
    }

    @Test
    public void testGetImageById() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Image.TABLE_NAME);

        Map<String, Object> imgMap = new HashMap<>();
        imgMap.put("image_id", IMG_ID);
        imgMap.put("image", IMG_DATA);

        imageInsert.execute(imgMap);

        // Exercise
        Optional<Image> img = imageDao.findImageById(IMG_ID);

        // Post exercise
        Assert.assertTrue(img.isPresent());
        Assert.assertEquals(IMG_ID, img.get().getId());
        Assert.assertArrayEquals(IMG_DATA, img.get().getData());
    }

    @Test
    public void testGetImageByNotPresentId() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, Image.TABLE_NAME);

        // Exercise
        Optional<Image> img = imageDao.findImageById(IMG_ID);

        // Post conditions
        Assert.assertFalse(img.isPresent());
    }
}