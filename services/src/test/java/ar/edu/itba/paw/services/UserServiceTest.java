package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PasswordResetTokenDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.UserVerificationTokenDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@RunWith(MockitoJUnitRunner.Silent.class)

@Transactional
public class UserServiceTest {

    private static final long ID = 1L;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encoded";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String DESCRIPTION = "description";
    private static final String TOKEN = "token";
    private static final byte[] IMAGE = new byte[10];
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 2;
    private static final int TOTAL_COUNT = 4;
    private static final long AVATAR_ID = 69;
    private static final long DEFAULT_AVATAR_ID = 0;

    @Mock
    private UserDao dao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserVerificationTokenDao userVerificationTokenDao;

    @Mock
    private PasswordResetTokenDao passwordResetTokenDao;

    @Mock
    private MailService mailService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private final UserServiceImpl userService = new UserServiceImpl();

    /*
     * - register                   -> 0 (tested)
     * - updateAvatar               -> 1
     * - getAvatar                  -> 1
     * - createConfirmationEmail    -> 2
     * - createPasswordResetEmail   -> 2
     * - confirmRegistration        -> 1
     * - validatePasswordResetToken -> 1
     * - updatePassword             -> 2
     */

    // Verifico que el service retorne el user que le dio el DAO y para generarlo haya utilizado la imagen que subio
    @Test
    public void testRegisterWithAvatar() throws DuplicateUniqueUserAttributeException {

        User userMock = Mockito.when(Mockito.mock(User.class).getAvatarId()).thenReturn(AVATAR_ID).getMock();

        UserService userServiceSpy = Mockito.spy(userService);

        Image imgMock = Mockito.when(Mockito.mock(Image.class).getId()).thenReturn(AVATAR_ID).getMock();

        Mockito.when(imageService.uploadImage(Mockito.any(byte[].class), Mockito.anyString())).thenReturn(imgMock);

        Mockito.when(dao.register(
                Mockito.anyString(), // username
                Mockito.anyString(), // password
                Mockito.anyString(), // name
                Mockito.anyString(), // email
                Mockito.anyString(), // description
                Mockito.anyString(), // language
                Mockito.anySet(),    // roles
                Mockito.any(Image.class), // image
                Mockito.anyBoolean() // enabled
            )).thenReturn(userMock);

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(PASSWORD);

        Mockito.doNothing().when(userServiceSpy).createConfirmationEmail(
                Mockito.any(User.class),
                Mockito.anyString(),
                Mockito.any(Locale.class));

        User user = userServiceSpy.register(USERNAME,PASSWORD, NAME, EMAIL, DESCRIPTION, IMAGE, "", Locale.ENGLISH);

        // Quiero revisar que el register haya utilizado el mock que le da el image service
        Mockito.verify(dao).register(Mockito.anyString(), // username
                Mockito.anyString(), // password
                Mockito.anyString(), // name
                Mockito.anyString(), // email
                Mockito.anyString(), // description
                Mockito.anyString(), // language
                Mockito.anySet(),    // roles
                Mockito.eq(imgMock), // image mock
                Mockito.anyBoolean() // enabled
        );
        Assert.assertEquals(AVATAR_ID, user.getAvatarId());
    }

    @Test
    public void testRegisterWithoutAvatar() throws DuplicateUniqueUserAttributeException {

        User userMock = Mockito.when(Mockito.mock(User.class).getAvatarId()).thenReturn(DEFAULT_AVATAR_ID).getMock();

        UserService userServiceSpy = Mockito.spy(userService);

        Mockito.when(dao.register(
                Mockito.anyString(),    // username
                Mockito.anyString(),    // password
                Mockito.anyString(),    // name
                Mockito.anyString(),    // email
                Mockito.anyString(),    // description
                Mockito.anyString(),    // language
                Mockito.anySet(),       // roles
                Mockito.any(),       // image
                Mockito.anyBoolean()    // enabled
        )).thenReturn(userMock);

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(PASSWORD);

        Mockito.doNothing().when(userServiceSpy).createConfirmationEmail(
                Mockito.any(User.class),
                Mockito.anyString(),
                Mockito.any(Locale.class));

        User user = userServiceSpy.register(USERNAME, PASSWORD, NAME, EMAIL, DESCRIPTION, new byte[]{}, "", Locale.ENGLISH);

        Mockito.verify(dao).register(Mockito.anyString(), // username
                Mockito.anyString(),    // password
                Mockito.anyString(),    // name
                Mockito.anyString(),    // email
                Mockito.anyString(),    // description
                Mockito.anyString(),    // language
                Mockito.anySet(),       // roles
                Mockito.isNull(),       // no hay img -> null
                Mockito.anyBoolean()    // enabled
        );

        Assert.assertEquals(DEFAULT_AVATAR_ID, user.getAvatarId());
    }

    @Test
    public void testRegisterPasswordEncoded() throws DuplicateUniqueUserAttributeException {

        User userMock = Mockito.when(Mockito.mock(User.class).getPassword()).thenReturn(ENCODED_PASSWORD).getMock();

        UserService userServiceSpy = Mockito.spy(userService);

        Mockito.when(imageService.uploadImage(Mockito.any(byte[].class), Mockito.anyString())).thenReturn(Mockito.mock(Image.class));

        Mockito.when(dao.register(
                Mockito.anyString(),    // username
                Mockito.anyString(),    // password
                Mockito.anyString(),    // name
                Mockito.anyString(),    // email
                Mockito.anyString(),    // description
                Mockito.anyString(),    // language
                Mockito.anySet(),       // roles
                Mockito.any(),          // image
                Mockito.anyBoolean()    // enabled
        )).thenReturn(userMock);

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(ENCODED_PASSWORD);

        Mockito.doNothing().when(userServiceSpy).createConfirmationEmail(
                Mockito.any(User.class),
                Mockito.anyString(),
                Mockito.any(Locale.class)
        );

        User user = userServiceSpy.register(USERNAME, PASSWORD, NAME, EMAIL, DESCRIPTION, IMAGE, "", Locale.ENGLISH);


        Mockito.verify(dao).register(
                Mockito.anyString(),            // username
                Mockito.eq(ENCODED_PASSWORD),   // password
                Mockito.anyString(),            // name
                Mockito.anyString(),            // email
                Mockito.anyString(),            // description
                Mockito.anyString(),            // language
                Mockito.anySet(),               // roles
                Mockito.any(),                  // image
                Mockito.anyBoolean()            // enabled
        );
        Assert.assertEquals(ENCODED_PASSWORD, user.getPassword());
    }
}
