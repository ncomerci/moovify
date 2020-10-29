package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageDao {

    Image uploadImage(byte[] data, String securityTag);

    Optional<Image> findImageById(long imageId);

    Optional<Image> getImage(long imageId, String securityTag);

    void deleteImage(Image image);
}
