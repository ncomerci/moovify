package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Autowired
    private ImageDao imageDao;

    @Transactional
    @Override
    public Image uploadImage(byte[] image, String securityTag) {
        return imageDao.uploadImage(image, securityTag);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<byte[]> getImage(long imageId, String securityTag) {
        return imageDao.getImage(imageId, securityTag).map(Image::getData);
    }

    @Override
    public byte[] getImage(String imagePath) {
        try {
            final byte[] ans = Files.readAllBytes(Paths.get(Objects.requireNonNull(this.getClass().getClassLoader().getResource(imagePath)).toURI()));

            LOGGER.debug("{} image resource loaded successfully", imagePath);

            return ans;
        }
        catch(IOException ioE) {
            LOGGER.error("Could not locate {} image resource", imagePath, ioE);
            throw new RuntimeException("Could not locate image resource");
        }
        catch(URISyntaxException uriSyntaxE) {
            LOGGER.error("There was a problem with URI Syntax searching {} image resource", imagePath, uriSyntaxE);
            throw new RuntimeException("Could not locate image resource");
        }
    }

    @Transactional
    @Override
    public void deleteImage(long imageId) {
        imageDao.deleteImage(imageId);
    }
}
