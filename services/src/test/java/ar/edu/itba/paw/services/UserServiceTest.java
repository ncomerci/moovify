package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PasswordResetTokenDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.UserVerificationTokenDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.Silent.class)

@Transactional
public class UserServiceTest {

    private static final long ID = 1L;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String DESCRIPTION = "description";
    private static final String TOKEN = "token";
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 2;
    private static final int TOTAL_COUNT = 4;
    private static final long AVATAR_ID = 0;

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


    @Test
    public void testRegister() throws DuplicateUniqueUserAttributeException {
//        1. Setup: Establezco las pre-condiciones

        User user = Mockito.mock(User.class);
        UserService userServiceMock = Mockito.mock(UserService.class);


        Mockito.when(dao.register(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Locale.ENGLISH.getLanguage(), Mockito.any(), Mockito.eq(null), Mockito.eq(true)))
                .thenReturn(user);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(PASSWORD);
        Mockito.doNothing().when(userServiceMock).createConfirmationEmail(Mockito.isA(User.class),Mockito.anyString(), Locale.ENGLISH);


//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        User user1 = userService.register(USERNAME,PASSWORD, NAME, EMAIL, DESCRIPTION, new byte[]{}, "", Locale.ENGLISH);

//        3. Validaciones: Confirmo las postcondiciones

        Assert.assertNotNull(user1);
        Assert.assertEquals(user, user1);
    }

    @Test
    public void testRegisterWithAvatar() throws DuplicateUniqueUserAttributeException {
//        1. Setup: Establezco las pre-condiciones
        User user = Mockito.mock(User.class);
        UserService userServiceMock = Mockito.mock(UserService.class);


//        Mockito.when(dao.register(Mockito.anyString(),Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyCollection(), Mockito.anyLong(), Mockito.anyBoolean()))
//                .thenReturn(user);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(PASSWORD);
//        Mockito.when(imageService.uploadImage(Mockito.any(byte[].class), Mockito.anyString())).thenReturn(AVATAR_ID);
        Mockito.doNothing().when(userServiceMock).createConfirmationEmail(Mockito.isA(User.class),Mockito.anyString(), Locale.ENGLISH);

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        User user1 = userService.register(USERNAME,PASSWORD, NAME, EMAIL, DESCRIPTION, new byte[]{8}, "", Locale.ENGLISH);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertNotNull(user1);
        Assert.assertEquals(user, user1);
        Assert.assertEquals(AVATAR_ID, user1.getId());
    }

    @Test
    public void testUpdateName() {
        userService.updateName(Mockito.mock(User.class), NAME);
    }

    @Test
    public void testUpdateUsername() throws DuplicateUniqueUserAttributeException {
        userService.updateUsername(Mockito.mock(User.class), USERNAME);
    }

    @Test
    public void testUpdateDescription() {
        userService.updateDescription(Mockito.mock(User.class), DESCRIPTION);
    }

    @Test()
    public void updatePassword() {

        userService.updatePassword(Mockito.mock(User.class), PASSWORD);
    }

    @Test
    public void testDeleteUser() {
        userService.deleteUser(Mockito.mock(User.class));
    }


    @Test
    public void restoreUser() {
        userService.restoreUser(Mockito.mock(User.class));
    }

    @Test
    public void testPromoteUserToAdmin() {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.USER);

        User user = Mockito.when(Mockito.mock(User.class).getRoles()).thenReturn(roles).getMock();
        userService.promoteUserToAdmin(user);

        Assert.assertTrue(user.getRoles().stream().anyMatch(role -> role.name().equals(Role.ADMIN.name())));
    }

    @Test(expected = NullPointerException.class)
    public void testPromoteUserToAdminNull() {

        userService.promoteUserToAdmin(null);
    }

//    @Test
//    public void testConfirmRegistration() {
//
//        UserVerificationToken userVerificationToken = Mockito.mock(UserVerificationToken.class);
//        List<Role> roles = new ArrayList<>();
//        roles.add(Role.NOT_VALIDATED);
//        User user = Mockito.when(Mockito.mock(User.class).getRoles()).thenReturn(roles).getMock();
//
//        Mockito.when(userVerificationToken.isValid()).thenReturn(true);
//        Mockito.when(userVerificationTokenDao.getVerificationToken(Mockito.anyString())).thenReturn(Optional.of(userVerificationToken));
//        Mockito.when(userVerificationToken.getUser()).thenReturn( user);
//        Mockito.doNothing().when(userVerificationTokenDao).deleteVerificationToken(user);
//
//        final Optional<User> user1 = userService.confirmRegistration(TOKEN);
//
//        Assert.assertTrue(user1.isPresent());
//        Assert.assertTrue(user1.get().getRoles().stream().anyMatch(role -> role.name().equals(Role.USER.name())));
//    }

    @Test
    public void testCreateConfirmationEmail() {

        //        1. Setup: Establezco las pre-condiciones

        User user = Mockito.mock(User.class);
        UserVerificationToken userVerificationToken = Mockito.mock(UserVerificationToken.class);

        Mockito.when(userVerificationTokenDao.createVerificationToken(Mockito.anyString(),Mockito.isA(LocalDateTime.class), Mockito.isA(User.class))).thenReturn(userVerificationToken);
        Mockito.doNothing().when(mailService).sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyMap(), Locale.ENGLISH);


//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        userService.createConfirmationEmail(user," ", Locale.ENGLISH);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateConfirmationEmailNull() {

        userService.createConfirmationEmail(null," ", Locale.ENGLISH);
    }

    @Test
    public void testCreatePasswordResetEmail() {

        //        1. Setup: Establezco las pre-condiciones

        User user = Mockito.mock(User.class);
        PasswordResetToken passwordResetToken = Mockito.mock(PasswordResetToken.class);

        Mockito.when(passwordResetTokenDao.createPasswordResetToken(Mockito.anyString(),Mockito.isA(LocalDateTime.class), Mockito.isA(User.class))).thenReturn(passwordResetToken);
        Mockito.doNothing().when(mailService).sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyMap(), Locale.ENGLISH);

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        userService.createPasswordResetEmail(user, " ", Locale.ENGLISH);

    }
    @Test(expected = NullPointerException.class)
    public void testCreatePasswordResetEmailNull() {
        userService.createPasswordResetEmail(null, " ", Locale.ENGLISH);
    }


//    @Test
//    public void testUpdatePassword() {
//        PasswordResetToken passwordResetToken = Mockito.mock(PasswordResetToken.class);
//        User user = Mockito.when(Mockito.mock(User.class).getPassword()).thenReturn(PASSWORD).getMock();
//
//        Mockito.when(passwordResetToken.isValid()).thenReturn(true);
//        Mockito.when(passwordResetTokenDao.getResetPasswordToken(Mockito.anyString())).thenReturn(Optional.of(passwordResetToken));
//        Mockito.when(passwordResetToken.getUser()).thenReturn(user);
//        Mockito.doNothing().when(passwordResetTokenDao).deletePasswordResetToken(user);
//
//        final Optional<User> user1 = userService.updatePassword("password2", TOKEN);
//
//        Assert.assertTrue(user1.isPresent());
//    }
    /*TODO testear get avatar*/

    @Test
    public void findUserById() {
        User user = Mockito.mock(User.class);

        Mockito.when(dao.findUserById(Mockito.longThat(e -> e > 0))).thenReturn(Optional.of(user));

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        Optional<User> user1 = userService.findUserById(ID);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertTrue(user1.isPresent());
        Assert.assertEquals(user, user1.get());
    }

    @Test
    public void findUserByUsername() {
        User user = Mockito.mock(User.class);

        Mockito.when(dao.findUserByUsername(Mockito.anyString())).thenReturn(Optional.of(user));

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        Optional<User> user1 = userService.findUserByUsername(USERNAME);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertTrue(user1.isPresent());
        Assert.assertEquals(user, user1.get());
    }

    @Test
    public void findUserByEmail() {
        User user = Mockito.mock(User.class);

        Mockito.when(dao.findUserByEmail(Mockito.anyString())).thenReturn(Optional.of(user));

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        Optional<User> user1 = userService.findUserByEmail(EMAIL);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertTrue(user1.isPresent());
        Assert.assertEquals(user, user1.get());
    }

    @Test
    public void getAllUsers() {
        PaginatedCollection<User> users = new PaginatedCollection<>(new ArrayList<>(), PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT );
        Mockito.when(dao.getAllUsers(
                Mockito.any(),
                Mockito.intThat(e -> e >= 0),
                Mockito.intThat(e -> e > 0))
        ).thenReturn(users);

        PaginatedCollection<User> users1 = userService.getAllUsers(PAGE_NUMBER, PAGE_SIZE);

        Assert.assertEquals(users, users1);
    }
}
