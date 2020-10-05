package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageDao imageDao;

    @Override
    public long uploadImage(byte[] image, String securityTag) {
        return imageDao.uploadImage(image, securityTag);
    }

    @Override
    public Optional<byte[]> getImage(long imageId, String securityTag) {
        return imageDao.getImage(imageId, securityTag);
    }

    @Override
    public byte[] getImage(String imagePath) throws IOException, URISyntaxException {
        return Files.readAllBytes(Paths.get(Objects.requireNonNull(this.getClass().getClassLoader().getResource(imagePath)).toURI()));
    }

    @Override
    public void deleteImage(long imageId) {
        imageDao.deleteImage(imageId);
    }
}
