package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.models.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class ImageDaoImpl implements ImageDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Image uploadImage(byte[] data, String securityTag) {

        final Image image = new Image(data, securityTag);

        em.persist(image);

        LOGGER.info("Created Image {} with Security Tag {}", image.getId(), securityTag);

        return image;
    }

    @Override
    public Optional<Image> findImageById(long imageId) {

        LOGGER.info("Get Image by Id {}", imageId);

        return Optional.ofNullable(em.find(Image.class, imageId));
    }

    @Override
    public Optional<Image> getImage(long imageId, String securityTag) {

        LOGGER.info("Get Image {} with Security Tag {}", imageId, securityTag);

        final Image image = em.find(Image.class, imageId);

        if(image != null && !image.getTag().equals(securityTag)) {
            LOGGER.error("Attempted access to image {} with incorrect security tag {}", imageId, securityTag);
            return Optional.empty();
        }

        return Optional.ofNullable(image);
    }
}
