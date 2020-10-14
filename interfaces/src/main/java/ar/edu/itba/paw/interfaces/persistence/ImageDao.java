package ar.edu.itba.paw.interfaces.persistence;

import java.util.Optional;

public interface ImageDao {

    long uploadImage(byte[] image, String securityTag);

    Optional<byte[]> getImage(long imageId, String securityTag);

    void deleteImage(long imageId);
}
