package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceImplTest {

    private static final String SECURITY_TAG = "SECURITY_TAG";
    private static final String IMAGE_PATH = "test.jpg";
    private static final long IMAGE_ID = 14;
    @Mock
    private ImageDao imageDao;

    @InjectMocks
    private final ImageServiceImpl imageService = new ImageServiceImpl();

    @Test
    public void testUploadImage() {

//        Mockito.when(imageDao.uploadImage(Mockito.isA(byte[].class), Mockito.anyString())).thenReturn(IMAGE_ID);
//
//        Image image = imageService.uploadImage(new byte[]{}, SECURITY_TAG);
//
//        Assert.assertEquals(IMAGE_ID, image.getId());
    }

    @Test
    public void testGetImage() {

//        Mockito.when(imageDao.getImage(Mockito.longThat(e -> e > 0), Mockito.anyString())).thenReturn(Optional.of(new byte[]{}));

        imageService.getImage(IMAGE_ID, SECURITY_TAG);
    }

    @Test
    public void testGetImagePath() {

        byte[] image = imageService.getImage(IMAGE_PATH);

        Assert.assertTrue(image.length > 0);
    }

    @Test(expected = RuntimeException.class)
    public void testGetImagePathFail() {

        imageService.getImage(null);
    }
}