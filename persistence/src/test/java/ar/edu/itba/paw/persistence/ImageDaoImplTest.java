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

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ImageDaoImplTest {

    private static final int BYTE_ARRAY_SIZE = 100;
    private static final int IMG_ID = 6;
    private static final byte[] IMG_DATA = new byte[BYTE_ARRAY_SIZE];
    private static final String TAG = "tag";
    private static final String OTHER_TAG = "alter_tag";

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

    @Rollback
    @Test
    public void testUploadImage() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Image.TABLE_NAME);

        imageDao.uploadImage(new byte[BYTE_ARRAY_SIZE], TAG);

        em.flush();

        final int count = JdbcTestUtils.countRowsInTable(jdbcTemplate, Image.TABLE_NAME);

        Assert.assertEquals(1, count);
    }

    @Rollback
    @Test
    public void testFindImageById() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Image.TABLE_NAME);

        Map<String, Object> imgMap = new HashMap<>();
        imgMap.put("image_id", IMG_ID);
        imgMap.put("image", IMG_DATA);
        imgMap.put("security_tag", TAG);

        imageInsert.execute(imgMap);

        Optional<Image> img = imageDao.findImageById(IMG_ID);

        Assert.assertTrue(img.isPresent());
        Assert.assertEquals(IMG_ID, img.get().getId());
        Assert.assertEquals(TAG, img.get().getTag());
        Assert.assertArrayEquals(IMG_DATA, img.get().getData());
    }

    @Rollback
    @Test
    public void testFindImageByNotPresentId() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Image.TABLE_NAME);

        Optional<Image> img = imageDao.findImageById(IMG_ID);

        Assert.assertFalse(img.isPresent());
    }

    @Rollback
    @Test
    public void testGetImageById() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Image.TABLE_NAME);

        Map<String, Object> imgMap = new HashMap<>();
        imgMap.put("image_id", IMG_ID);
        imgMap.put("image", IMG_DATA);
        imgMap.put("security_tag", TAG);

        imageInsert.execute(imgMap);

        Optional<Image> img = imageDao.getImage(IMG_ID, TAG);

        Assert.assertTrue(img.isPresent());
        Assert.assertEquals(IMG_ID, img.get().getId());
        Assert.assertEquals(TAG, img.get().getTag());
        Assert.assertArrayEquals(IMG_DATA, img.get().getData());
    }

    @Rollback
    @Test
    public void testGetImageByNotPresentId() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Image.TABLE_NAME);

        Optional<Image> img = imageDao.getImage(IMG_ID, TAG);

        Assert.assertFalse(img.isPresent());
    }

    @Rollback
    @Test
    public void testGetImageByIdNonMatchingTag() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, Image.TABLE_NAME);

        Map<String, Object> imgMap = new HashMap<>();
        imgMap.put("image_id", IMG_ID);
        imgMap.put("image", IMG_DATA);
        imgMap.put("security_tag", TAG);

        imageInsert.execute(imgMap);

        Optional<Image> img = imageDao.getImage(IMG_ID, OTHER_TAG);


        Assert.assertFalse(img.isPresent());
    }
}