package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class UserDaoImplTest {

    private static final Long ID = 1L;
    private static final Long USER1_ID = InsertHelper.USER1_ID;
    private static final Long USER2_ID = InsertHelper.USER2_ID;
    private static final String USERS = User.TABLE_NAME;
    private static final String POSTS = Post.TABLE_NAME;
    private static final String USERNAME = "Username";
    private static final String LOCALE_LANGUAGE = Locale.ENGLISH.getLanguage();
    private static final String USERNAME2 = "Username2";
    private static final String PASSWORD = "Password";
    private static final String EMAIL = "Email";
    private static final String NAME = "Name";
    private static final String NAME2 = "Name2";
    private static final String DESCRIPTION = "Description";
    private static final LocalDateTime CREATION_DATE = LocalDateTime.of(2020, 8, 6, 11,55);
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

        // Pre conditions
        User user = em.find(User.class, USER1_ID);

        // Exercise
        userDao.updateUsername(user, "myNewUsername");

        // Post conditions
        Assert.assertEquals("myNewUsername", user.getUsername());
    }

    @Test(expected = DuplicateUniqueUserAttributeException.class)
    @Sql("classpath:user1.sql")
    public void testUpdateUsernameSameUsername() throws DuplicateUniqueUserAttributeException {

        // Pre conditions
        User user = em.find(User.class, USER1_ID);

        // Exercise
        userDao.updateUsername(user, user.getUsername());
    }

    @Test(expected = DuplicateUniqueUserAttributeException.class)
    @Sql("classpath:user1.sql")
    @Sql("classpath:user2.sql")
    public void testUpdateUsernameTakenUsername() throws DuplicateUniqueUserAttributeException {

        // Pre conditions
        User user1 = em.find(User.class, USER1_ID);
        User user2 = em.find(User.class, USER2_ID);

        // Exercise
        userDao.updateUsername(user1, user2.getUsername());
    }

    @Test
    public void testFindUserById() {

        // Pre conditions
        long id = helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE, "USER");

        // Exercise
        final Optional<User> user = userDao.findUserById(id);

        final String whereClause = String.format("user_id = %d", id);

        // Post conditions
        Assert.assertTrue(user.isPresent());
        Assert.assertEquals(id,user.get().getId() );
        Assert.assertEquals(USERNAME, user.get().getUsername() );
        Assert.assertEquals(EMAIL, user.get().getEmail());
    }

    @Test
    public void testFindUserByUsername() {

        // Pre conditions
        long id = helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE, "USER");

        // Exercise
        final Optional<User> user = userDao.findUserByUsername(USERNAME);

        // Post conditions
        Assert.assertTrue(user.isPresent() );
        Assert.assertEquals(USERNAME, user.get().getUsername() );
        Assert.assertEquals(EMAIL, user.get().getEmail());
    }

    @Test
    public void testFindUserByEmail() {

        // Pre conditions
        long id = helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE, "USER");

        // Exercise
        final Optional<User> user = userDao.findUserByEmail(EMAIL);

        // Post conditions
        Assert.assertTrue(user.isPresent());
        Assert.assertEquals(USERNAME, user.get().getUsername() );
        Assert.assertEquals(EMAIL, user.get().getEmail());
    }

    @Test
    public void testGetAllUsersOldest() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, USERS);
        long user1 = helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE, "USER");
        long user2 = helper.insertUser(USERNAME2, NAME, CREATION_DATE.plusHours(10), "email2", ENABLE, "USER");
        long user3 = helper.insertUser("username3" , NAME, CREATION_DATE.plusHours(8), "email3", ENABLE, "USER");
        long user4 = helper.insertUser("username4", NAME, CREATION_DATE.plusHours(20), "email4", ENABLE, "USER");

        // Exercise
        PaginatedCollection<User> users = userDao.getAllUsers(true, UserDao.SortCriteria.OLDEST, PAGE_SECOND, PAGE_SIZE);

        // Post conditions
        Assert.assertEquals(4, users.getTotalCount());
        Assert.assertArrayEquals(new Long[]{user2, user4}, users.getResults().stream().map(User::getId).toArray());
    }

    @Test
    public void testGetAllUsersNewest() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, USERS);

        long user1 = helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE, "USER");
        long user2 = helper.insertUser(USERNAME2, NAME, CREATION_DATE.plusHours(10), "email2", ENABLE, "USER");
        long user3 = helper.insertUser("username3", NAME, CREATION_DATE.plusHours(8), "email3", ENABLE, "USER");
        long user4 = helper.insertUser("username4", NAME, CREATION_DATE.plusHours(20), "email4", ENABLE, "USER");

        // Exercise
        PaginatedCollection<User> users = userDao.getAllUsers(true, UserDao.SortCriteria.NEWEST, PAGE_SECOND, PAGE_SIZE);

        // Post conditions
        Assert.assertEquals(4, users.getTotalCount());
        Assert.assertArrayEquals(new Long[]{user3, user1}, users.getResults().stream().map(User::getId).toArray());
    }

    @Test
    public void testGetAllUsersName() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, USERS);

        long user1 = helper.insertUser(USERNAME, "name", CREATION_DATE, EMAIL, ENABLE, "USER");
        long user2 = helper.insertUser(USERNAME2, "xname", CREATION_DATE.plusHours(10), "email2", ENABLE, "USER");
        long user3 = helper.insertUser("username3", "yname", CREATION_DATE.plusHours(8), "email3", ENABLE, "USER");
        long user4 = helper.insertUser("username4", "zname", CREATION_DATE.plusHours(20), "email4", ENABLE, "USER");

        // Exercise
        PaginatedCollection<User> users = userDao.getAllUsers(true, UserDao.SortCriteria.USERNAME, PAGE_SECOND, PAGE_SIZE);

        // Post conditions
        Assert.assertEquals(4, users.getTotalCount());
        Assert.assertArrayEquals(new String[]{"yname", "zname"}, users.getResults().stream().map(User::getName).toArray());
    }

    @Test
    @Sql("classpath:movies.sql")
    @Sql("classpath:categories.sql")
    public void testGetAllUsersHottest() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate, USERS);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, POSTS);

        long user1 = helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE, "USER");
        long post1 = helper.insertPost(user1);
        long user2 = helper.insertUser(USERNAME2, NAME, CREATION_DATE, "email2", ENABLE, "USER");
        long post2 = helper.insertPost(user2);
        long user3 = helper.insertUser("username3", NAME, CREATION_DATE.plusHours(8), "email3", ENABLE, "USER");
        long user4 = helper.insertUser("username4", NAME, CREATION_DATE.plusHours(20), "email4", ENABLE, "USER");

        helper.insertPostLike(post1, user1, UPVOTE);
        helper.insertPostLike(post2, user1, UPVOTE);
        helper.insertPostLike(post1, user2, UPVOTE);
        helper.insertPostLike(post2, user2, UPVOTE);
        helper.insertPostLike(post1, user3, UPVOTE);

        // Exercise
        PaginatedCollection<User> users = userDao.getAllUsers(true, UserDao.SortCriteria.VOTES, PAGE_FIRST, PAGE_SIZE);

        // Post conditions
        Assert.assertEquals(4, users.getTotalCount());
        Assert.assertArrayEquals(new Long[]{user1, user2}, users.getResults().stream().map(User::getId).toArray());
    }

    @Test
    public void testSearchUsers() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate ,USERS);

        long user1ID = helper.insertUser("username", NAME, CREATION_DATE, EMAIL, ENABLE, "USER");
        long user2ID = helper.insertUser("usuario", NAME2, CREATION_DATE, "email2", ENABLE, "USER");
        long user3ID = helper.insertUser("userNAMEUSERname", "test", CREATION_DATE.plusHours(8), "email3", ENABLE, "USER");
        long user4ID = helper.insertUser("nombre", "test", CREATION_DATE.plusHours(20), "email4", ENABLE, "USER");

        // Exercise
        PaginatedCollection<User> users = userDao.searchUsers("useRName", true, UserDao.SortCriteria.NEWEST, 0, 10);

        // Post conditions
        Assert.assertEquals(2, users.getTotalCount());
        Assert.assertArrayEquals(new Long[]{user3ID, user1ID}, users.getResults().stream().map(User::getId).toArray());
    }

    @Rollback
    @Test
    public void testSearchUsersByRole() {

        // Pre conditions
        JdbcTestUtils.deleteFromTables(jdbcTemplate ,USERS);

        helper.insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE, "USER");
        helper.insertUser(USERNAME2, NAME, CREATION_DATE, "email2", ENABLE, "ADMIN");
        helper.insertUser("user", NAME, CREATION_DATE, "email3", ENABLE, "USER");

        // Exercise
        PaginatedCollection<User> users = userDao.searchUsersByRole(USERNAME, Role.USER, true, UserDao.SortCriteria.VOTES, PAGE_FIRST, PAGE_SIZE);

        // Post conditions
        Assert.assertEquals(1, users.getTotalCount());
    }

}
