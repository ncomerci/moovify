package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageService {

    Image uploadImage(byte[] image);

    Optional<byte[]> findImageById(long imageId);

    Optional<byte[]> findImageByPath(String imagePath);
}
