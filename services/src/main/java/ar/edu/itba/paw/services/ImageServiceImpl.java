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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Autowired
    private ImageDao imageDao;

    @Transactional
    @Override
    public Image uploadImage(byte[] image, String type) {
        return imageDao.uploadImage(image, type);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<byte[]> findImageById(long imageId) {
        return imageDao.findImageById(imageId).map(Image::getData);
    }

    @Override
    public Optional<byte[]> findImageByPath(String imagePath) {

        final URL imageUrl = this.getClass().getClassLoader().getResource(imagePath);

        if(imageUrl == null)
            return Optional.empty();

        try {
            final byte[] ans = Files.readAllBytes(Paths.get((imageUrl.toURI())));

            LOGGER.debug("{} image resource loaded successfully", imagePath);

            return Optional.of(ans);
        }
        catch(IOException ioE) {
            LOGGER.error("Could not locate {} image resource", imagePath, ioE);
            return Optional.empty();
        }
        catch(URISyntaxException uriSyntaxE) {
            LOGGER.error("There was a problem with URI Syntax searching {} image resource", imagePath, uriSyntaxE);
            return Optional.empty();
        }
    }
}
