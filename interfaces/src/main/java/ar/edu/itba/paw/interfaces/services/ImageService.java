package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

public interface ImageService {

    long uploadImage(byte[] image, String securityTag);

    Optional<byte[]> getImage(long imageId, String securityTag);

    byte[] getImage(String imagePath);

    void deleteImage(long imageId);
}
