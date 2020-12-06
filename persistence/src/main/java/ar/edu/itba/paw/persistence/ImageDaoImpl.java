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
    public Image uploadImage(byte[] data) {

        final Image image = new Image(data);

        em.persist(image);

        LOGGER.info("Created Image {}", image.getId());

        return image;
    }

    @Override
    public Optional<Image> findImageById(long imageId) {

        LOGGER.info("Get Image by Id {}", imageId);

        return Optional.ofNullable(em.find(Image.class, imageId));
    }
}
