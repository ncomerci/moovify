package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Optional;

@Repository
public class ImageDaoImpl implements ImageDao {

    private static final String IMAGES = TableNames.IMAGES.getTableName();

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert imageInsert;

    @Autowired
    public ImageDaoImpl(final DataSource ds){

        jdbcTemplate = new JdbcTemplate(ds);

        imageInsert = new SimpleJdbcInsert(ds)
                .withTableName(IMAGES)
                .usingGeneratedKeyColumns("image_id");
    }

    @Override
    public long uploadImage(byte[] image, String securityTag) {

        final HashMap<String, Object> map = new HashMap<>();

        map.put("image", image);
        map.put("security_tag", securityTag);

        return imageInsert.executeAndReturnKey(map).longValue();
    }

    @Override
    public Optional<byte[]> getImage(long imageId, String securityTag) {

        return jdbcTemplate.query(
                "SELECT " + IMAGES + ".image FROM " + IMAGES + " WHERE " + IMAGES + ".image_id = ? AND " + IMAGES + ".security_tag = ?",
                new Object[]{ imageId, securityTag }, (rs, rowNum) -> rs.getBytes("image")).stream().findFirst();
    }

    @Override
    public void deleteImage(long imageId) {
        jdbcTemplate.update("DELETE FROM " + IMAGES + " WHERE " + IMAGES + ".image_id = ?", imageId);
    }
}
