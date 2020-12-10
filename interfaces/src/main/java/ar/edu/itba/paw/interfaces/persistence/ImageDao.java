package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageDao {

    Image uploadImage(byte[] data, String type);

    Optional<Image> findImageById(long imageId);
}
