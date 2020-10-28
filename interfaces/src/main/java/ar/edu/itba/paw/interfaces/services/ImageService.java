package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageService {

    Image uploadImage(byte[] image, String securityTag);

    Optional<byte[]> getImage(long imageId, String securityTag);

    byte[] getImage(String imagePath);

    void deleteImage(long imageId);
}
