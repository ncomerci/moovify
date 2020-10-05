package ar.edu.itba.paw.interfaces.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public interface ImageService {

    long uploadImage(byte[] image, String securityTag);

    Optional<byte[]> getImage(long imageId, String securityTag);

    byte[] getImage(String imagePath) throws IOException, URISyntaxException;

    void deleteImage(long imageId);
}
