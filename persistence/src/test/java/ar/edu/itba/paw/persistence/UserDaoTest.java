package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import ar.edu.itba.paw.models.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
public class UserDaoTest {

    private static final Long ID = 1L;
    private static final Long USER1_ID = InsertHelper.USER1_ID;
    private static final Long USER2_ID = InsertHelper.USER2_ID;
    private static final User USER = Mockito.when(Mockito.mock(User.class).getId()).thenReturn(ID).getMock();
    private static final Post POST = Mockito.when(Mockito.mock(Post.class).getId()).thenReturn(ID).getMock();
    private static final String USERS = User.TABLE_NAME;
    private static final String USERS_ROLES = User.USER_ROLE_TABLE_NAME;
    private static final String POST_LIKES = PostLike.TABLE_NAME;
    private static final String POST_MOVIES = Post.POST_MOVIE_TABLE_NAME;
    private static final String POSTS = Post.TABLE_NAME;
    private static final String IMAGE = Image.TABLE_NAME;
    private static final String USERNAME = "Username";
    private static final String LOCALE_LANGUAGE = Locale.ENGLISH.getLanguage();
    private static final String USERNAME2 = "Username2";
    private static final String PASSWORD = "Password";
    private static final String EMAIL = "Email";
    private static final String NAME = "Name";
    private static final String NAME2 = "Name2";
    private static final String DESCRIPTION = "Description";
    private static final LocalDateTime CREATION_DATE = LocalDateTime.of(2020, 8, 6, 11,55);
    private static final long USER_ROLE = 1;
    private static final long ADMIN_ROLE = 2;
    private static final int PAGE_FIRST = 0;
    private static final int PAGE_SECOND = 1;
    private static final int PAGE_SIZE = 2;
    private static final boolean ENABLE = true;
    private static final boolean DISABLE = false;
    private static final int UPVOTE = 1;

    

    @Autowired
    private UserDaoImpl userDao;

    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    private InsertHelper helper;
    
    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.helper = new InsertHelper(jdbcTemplate);
    }

    @Test
    public void testRegister() throws DuplicateUniqueUserAttributeException {

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        roles.add(Role.ADMIN);

        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, USERS,"username = ?", USERNAME );

        User user = userDao.register(USERNAME, PASSWORD, NAME, EMAIL, DESCRIPTION, LOCALE_LANGUAGE, roles, null, ENABLE);

        em.flush();

        final String whereClause = String.format("user_id = %d", user.getId());

        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause)
        );
    }

    @Test
    @Sql("classpath:user1.sql")
    public void testUpdateUsername() throws DuplicateUniqueUserAttributeException {

        User user = em.find(User.class, USER1_ID);

        userDao.updateUsername(user, "myNewUsername");

        Assert.assertEquals("myNewUsername", user.getUsername());
    }

    @Test(expected = DuplicateUniqueUserAttributeException.class)
    @Sql("classpath:user1.sql")
    public void testUpdateUsernameSameUsername() throws DuplicateUniqueUserAttributeException {

        User user = em.find(User.class, USER1_ID);

        userDao.updateUsername(user, user.getUsername());
    }


    @Test(expected = DuplicateUniqueUserAttributeException.class)
    @Sql("classpath:user1.sql")
    @Sql("classpath:user2.sql")
    public void testUpdateUsernameTakenUsername() throws DuplicateUniqueUserAttributeException {

        User user1 = em.find(User.class, USER1_ID);
        User user2 = em.find(User.class, USER2_ID);

        userDao.updateUsername(user1, user2.getUsername());
    }

    @Test
    public void testFindUserById() {

        long id = helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);

        final Optional<User> user = userDao.findUserById(id);

        final String whereClause = String.format("user_id = %d", id);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause));
        Assert.assertTrue(user.isPresent());
        Assert.assertEquals(id,user.get().getId() );
        Assert.assertEquals(USERNAME, user.get().getUsername() );
        Assert.assertEquals(EMAIL, user.get().getEmail());
    }

    @Test
    public void testFindUserByUsername() {

        long id = helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);

        final Optional<User> user = userDao.findUserByUsername(USERNAME);

        Assert.assertTrue(user.isPresent() );

        final String whereClause = String.format("username = '%s'", USERNAME);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause));
        Assert.assertEquals(USERNAME, user.get().getUsername() );
        Assert.assertEquals(EMAIL, user.get().getEmail());

    }

    @Test
    public void testFindUserByEmail() {

        long id = helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);

        final Optional<User> user = userDao.findUserByEmail(EMAIL);

        Assert.assertTrue(user.isPresent());

        final String whereClause = String.format("email = '%s'", EMAIL);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause));
        Assert.assertEquals(USERNAME, user.get().getUsername() );
        Assert.assertEquals(EMAIL, user.get().getEmail());
    }

    @Test
    public void testGetAllUsersOldest() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, USERS);
        long user1 = helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);
        long user2 = helper.insertUser(USERNAME2, NAME, CREATION_DATE.plusHours(10), "email2", ENABLE);
        long user3 = helper.insertUser("username3" , NAME, CREATION_DATE.plusHours(8), "email3", ENABLE);
        long user4 = helper.insertUser("username4", NAME, CREATION_DATE.plusHours(20), "email4", ENABLE);

        PaginatedCollection<User> users = userDao.getAllUsers(UserDao.SortCriteria.OLDEST, PAGE_SECOND, PAGE_SIZE);
        Assert.assertEquals(4, users.getTotalCount());
        Assert.assertArrayEquals(new Long[]{user2, user4}, users.getResults().stream().map(User::getId).toArray());
    }

    @Test
    public void testGetAllUsersNewest() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, USERS);

        long user1 = helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);
        long user2 = helper.insertUser(USERNAME2, NAME, CREATION_DATE.plusHours(10), "email2", ENABLE);
        long user3 = helper.insertUser("username3", NAME, CREATION_DATE.plusHours(8), "email3", ENABLE);
        long user4 = helper.insertUser("username4", NAME, CREATION_DATE.plusHours(20), "email4", ENABLE);

        PaginatedCollection<User> users = userDao.getAllUsers(UserDao.SortCriteria.NEWEST, PAGE_SECOND, PAGE_SIZE);
        Assert.assertEquals(4, users.getTotalCount());
        Assert.assertArrayEquals(new Long[]{user3, user1}, users.getResults().stream().map(User::getId).toArray());
    }

    @Test
    public void testGetAllUsersName() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, USERS);

        long user1 = helper.insertUser(USERNAME, "name", CREATION_DATE, EMAIL, ENABLE);
        long user2 = helper.insertUser(USERNAME2, "xname", CREATION_DATE.plusHours(10), "email2", ENABLE);
        long user3 = helper.insertUser("username3", "yname", CREATION_DATE.plusHours(8), "email3", ENABLE);
        long user4 = helper.insertUser("username4", "zname", CREATION_DATE.plusHours(20), "email4", ENABLE);

        PaginatedCollection<User> users = userDao.getAllUsers(UserDao.SortCriteria.USERNAME, PAGE_SECOND, PAGE_SIZE);
        Assert.assertEquals(4, users.getTotalCount());
        Assert.assertArrayEquals(new String[]{"yname", "zname"}, users.getResults().stream().map(User::getName).toArray());
    }

    @Test
    @Sql("classpath:movies.sql")
    @Sql("classpath:categories.sql")
    public void testGetAllUsersHottest() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate, USERS);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, POSTS);

        long user1 = helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);
        long post1 = helper.insertPost(user1);
        long user2 = helper.insertUser(USERNAME2, NAME, CREATION_DATE, "email2", ENABLE);
        long post2 = helper.insertPost(user2);
        long user3 = helper.insertUser("username3", NAME, CREATION_DATE.plusHours(8), "email3", ENABLE);
        long user4 = helper.insertUser("username4", NAME, CREATION_DATE.plusHours(20), "email4", ENABLE);

        helper.insertPostLike(post1, user1, UPVOTE);
        helper.insertPostLike(post2, user1, UPVOTE);
        helper.insertPostLike(post1, user2, UPVOTE);
        helper.insertPostLike(post2, user2, UPVOTE);
        helper.insertPostLike(post1, user3, UPVOTE);

        PaginatedCollection<User> users = userDao.getAllUsers(UserDao.SortCriteria.LIKES, PAGE_FIRST, PAGE_SIZE);
        Assert.assertEquals(4, users.getTotalCount());
        Assert.assertArrayEquals(new Long[]{user1, user2}, users.getResults().stream().map(User::getId).toArray());
    }

    @Test
    public void testSearchUsers() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate ,USERS);

        helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);
        helper.insertUser("usuario", NAME2, CREATION_DATE, "email2", ENABLE);
        helper.insertUser("ussssername3", "test", CREATION_DATE.plusHours(8), "email3", ENABLE);
        helper.insertUser("username4", "test", CREATION_DATE.plusHours(20), "email4", ENABLE);

        PaginatedCollection<User> users = userDao.searchUsers(USERNAME, UserDao.SortCriteria.USERNAME, PAGE_FIRST, PAGE_SIZE);
        Assert.assertEquals(2, users.getTotalCount());
        Assert.assertArrayEquals(new String[]{USERNAME, "username4"}, users.getResults().stream().map(User::getUsername).toArray());
    }

//    @Rollback
//    @Test
//    public void testSearchUsersByRole() {
//        JdbcTestUtils.deleteFromTables(jdbcTemplate ,USERS);
//
//        helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);
//        helper.insertUser(USERNAME2, NAME, CREATION_DATE, "email2", ENABLE);
//
//        PaginatedCollection<User> users = userDao.searchUsersByRole(USERNAME, Role.ADMIN_ROLE, UserDao.SortCriteria.LIKES, PAGE_FIRST, PAGE_SIZE);
//        Assert.assertEquals(0, users.getTotalCount());
//
//        PaginatedCollection<User> users2 = userDao.searchUsersByRole(USERNAME, Role.USER_ROLE, UserDao.SortCriteria.LIKES, PAGE_FIRST, PAGE_SIZE);
//        Assert.assertEquals(2, users2.getTotalCount());
//    }

    @Test
    public void searchDeletedUsers() {

        JdbcTestUtils.deleteFromTables(jdbcTemplate ,USERS);

        helper.insertUser(USERNAME, "NAME", CREATION_DATE, EMAIL, ENABLE);
        helper.insertUser("New USername", "NOmbre", CREATION_DATE, "email2", ENABLE);
        helper.insertUser("Alternate USERNAME", NAME, CREATION_DATE, "email3", DISABLE);
        helper.insertUser("USUARIO", "Nein", CREATION_DATE, "email4", DISABLE);

        PaginatedCollection<User> users = userDao.searchDeletedUsers(USERNAME, UserDao.SortCriteria.LIKES, PAGE_FIRST, PAGE_SIZE);
        Assert.assertEquals(1, users.getTotalCount());

    }
}
