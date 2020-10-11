package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateEmailException;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUsernameException;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class UserDaoTest {

    private static final Long ID = 1L;
    private static final User USER = Mockito.when(Mockito.mock(User.class).getId()).thenReturn(ID).getMock();
    private static final Post POST = Mockito.when(Mockito.mock(Post.class).getId()).thenReturn(ID).getMock();
    private static final String USERS = TableNames.USERS.getTableName();
    private static final String USERS_ROLES = TableNames.USER_ROLE.getTableName();
    private static final String POST_LIKES = TableNames.POSTS_LIKES.getTableName();
    private static final String POST_MOVIES = TableNames.POST_MOVIE.getTableName();
    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String IMAGE = TableNames.IMAGES.getTableName();
    private static final String USERNAME = "Username";
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

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert usersInsert;
    private SimpleJdbcInsert usersRolesInsert;
    private SimpleJdbcInsert postsLikesInsert;
    private SimpleJdbcInsert postsMoviesInsert;
    private SimpleJdbcInsert imageInsert;
    private SimpleJdbcInsert postsInsert;
    private HashMap<String, Object> map;


    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.usersInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(USERS)
                .usingGeneratedKeyColumns("user_id");
        this.usersRolesInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(USERS_ROLES);
        this.postsLikesInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(POST_LIKES);
        this.postsMoviesInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(POST_MOVIES);
        this.imageInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(IMAGE)
                .usingGeneratedKeyColumns("image_id");
        this.postsInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(POSTS)
                .usingGeneratedKeyColumns("post_id");

        map = new HashMap<>();
        LocalDateTime creationDate = LocalDateTime.now();
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("username", USERNAME);
        map.put("password", PASSWORD);
        map.put("name", NAME);
        map.put("email", EMAIL);
        map.put("description", DESCRIPTION);
        map.put("avatar_id", null);
        map.put("enabled", ENABLE);
    }

    @Rollback
    @Test
    public void testRegister() throws DuplicateUsernameException, DuplicateEmailException {
        List<String> roles = new ArrayList<>();
        roles.add(Role.USER_ROLE);
        roles.add(Role.ADMIN_ROLE);

        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate,USERS,"username = ?", USERNAME );

        User user = userDao.register(USERNAME, PASSWORD, NAME, EMAIL, DESCRIPTION, roles, null, ENABLE );
        final String whereClause = String.format("user_id = %d and email = '%s' and name = '%s' and description = '%s'", user.getId(), EMAIL, NAME, DESCRIPTION);

        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause)
        );
    }

    @Rollback
    @Test
    public void testUpdateName() {
        long id = insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, USERS, "name = ?", "testName");

        userDao.updateName( Mockito.when(Mockito.mock(User.class).getId()).thenReturn(id).getMock(), "testName");
        final String whereClause = String.format("name = '%s'", "testName");

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause));
    }

    @Rollback
    @Test
    public void testUpdateUsername() throws DuplicateUsernameException {
        long id = insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, USERS, "username = ?", "testUsername");

        userDao.updateUsername( Mockito.when(Mockito.mock(User.class).getId()).thenReturn(id).getMock(), "testUsername");
        final String whereClause = String.format("username = '%s'", "testUsername");

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause));
    }

    @Rollback
    @Test
    public void testUpdateDescription() {
        long id = insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, USERS, "description = ?", "testDescription");

        userDao.updateDescription( Mockito.when(Mockito.mock(User.class).getId()).thenReturn(id).getMock(), "testDescription");
        final String whereClause = String.format("description = '%s'", "testDescription");

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause));
    }

    @Rollback
    @Test
    public void testDeleteUser() {

        userDao.deleteUser( Mockito.when(Mockito.mock(User.class).getId()).thenReturn(insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE)).getMock());
        final String whereClause = String.format("username = '%s' and enabled = '%s'", USERNAME, "false");

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause));
    }

    @Rollback
    @Test
    public void testRestoreUser() {
        long id = insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);
        userDao.restoreUser( Mockito.when(Mockito.mock(User.class).getId()).thenReturn(id).getMock());
        final String whereClause = String.format("username = '%s' and enabled = '%s'", USERNAME, "true");

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause));
    }

    @Test
    public void testReplaceUserRole() {
        long id = insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);

        userDao.replaceUserRole( Mockito.when(Mockito.mock(User.class).getId()).thenReturn(id).getMock(), Role.ADMIN_ROLE, Role.USER_ROLE);

        final String whereClause = String.format("user_id = %d and role_id = %d",id, ADMIN_ROLE);

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS_ROLES, whereClause));

    }

    @Rollback
    @Test
    public void testHasUserLikedPositive() {
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, POST_LIKES, "user_id = ? and post_id = ?", ID, ID);

        HashMap<String, Object> mapLike = new HashMap<>();
        mapLike.put("user_id", ID);
        mapLike.put("post_id", ID);
        mapLike.put("value", 1);

        postsLikesInsert.execute(mapLike);

        Assert.assertEquals(1, userDao.hasUserLiked( USER, POST));

    }

    @Rollback
    @Test
    public void testHasUserLikedNegative() {
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, POST_LIKES, "user_id = ? and post_id = ?", ID, ID);
        HashMap<String, Object> mapLike = new HashMap<>();
        mapLike.put("user_id", ID);
        mapLike.put("post_id", ID);
        mapLike.put("value", -1);

        postsLikesInsert.execute(mapLike);

        Assert.assertEquals(-1, userDao.hasUserLiked( USER, POST));

    }

    @Rollback
    @Test
    public void testHasUserNotLiked() {
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, POST_LIKES, "user_id = ? and post_id = ?", ID, ID);

        Assert.assertEquals(0, userDao.hasUserLiked( USER, POST));

    }

    @Rollback
    @Test
    public void testAddRoles() {

        long id = insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);

        List<String> list =  new ArrayList<>();
        list.add(Role.ADMIN_ROLE);

        userDao.addRoles( Mockito.when(Mockito.mock(User.class).getId()).thenReturn(id).getMock(), list);
        final String whereClause = String.format("user_id = %d and role_id = %d",id, ADMIN_ROLE);

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS_ROLES, whereClause));
    }

    @Rollback
    @Test
    public void testUpdatePassword() {
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, USERS, "password = ?", "testPassword");

        userDao.updatePassword( Mockito.when(Mockito.mock(User.class).getId()).thenReturn(usersInsert.executeAndReturnKey(map).longValue()).getMock(), "testPassword");
        final String whereClause = String.format("password = '%s' ", "testPassword");

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause));
    }

    @Rollback
    @Test
    public void updateAvatarId() {
        HashMap<String, Object> mapImage = new HashMap<>();
        mapImage.put("image", new byte[]{8});
        mapImage.put("security_tag","AVATAR");
        long image_id = imageInsert.executeAndReturnKey(mapImage).longValue();

        HashMap<String, Object> mapImage2 = new HashMap<>();
        mapImage2.put("image", new byte[]{8});
        mapImage2.put("security_tag","AVATAR");
        long image_id2 = imageInsert.executeAndReturnKey(mapImage2).longValue();

        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, USERS, "avatar_id = ?", image_id2);

        map.put("avatar_id", image_id);

        userDao.updateAvatarId( Mockito.when(Mockito.mock(User.class).getId()).thenReturn(usersInsert.executeAndReturnKey(map).longValue()).getMock(), image_id2);

        final String whereClause = String.format("avatar_id = %d", image_id2);
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause));
    }

    @Rollback
    @Test
    public void testFindUserById() {
        long id = insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);

        final Optional<User> user = userDao.findUserById(id);

        final String whereClause = String.format("user_id = %d", id);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause));
        Assert.assertTrue(user.isPresent());
        Assert.assertEquals(id,user.get().getId() );
        Assert.assertEquals(USERNAME, user.get().getUsername() );
        Assert.assertEquals(EMAIL, user.get().getEmail());

    }

    @Rollback
    @Test
    public void testFindUserByUsername() {
        long id = insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);

        final Optional<User> user = userDao.findUserByUsername(USERNAME);

        Assert.assertTrue(user.isPresent() );

        final String whereClause = String.format("username = '%s'", USERNAME);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause));
        Assert.assertEquals(USERNAME, user.get().getUsername() );
        Assert.assertEquals(EMAIL, user.get().getEmail());

    }

    @Rollback
    @Test
    public void testFindUserByEmail() {
        long id = insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);

        final Optional<User> user = userDao.findUserByEmail(EMAIL);

        Assert.assertTrue(user.isPresent());

        final String whereClause = String.format("email = '%s'", EMAIL);
        Assert.assertEquals(1,
                JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USERS, whereClause));
        Assert.assertEquals(USERNAME, user.get().getUsername() );
        Assert.assertEquals(EMAIL, user.get().getEmail());
    }

    @Rollback
    @Test
    public void testGetAllUsersOldest() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, USERS);
        long user1 = insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);
        long user2 = insertUser(USERNAME2, NAME, CREATION_DATE.plusHours(10), "email2", ENABLE);
        long user3 = insertUser("username3" , NAME, CREATION_DATE.plusHours(8), "email3", ENABLE);
        long user4 = insertUser("username4", NAME, CREATION_DATE.plusHours(20), "email4", ENABLE);

        PaginatedCollection<User> users = userDao.getAllUsers(UserDao.SortCriteria.OLDEST, PAGE_SECOND, PAGE_SIZE);
        Assert.assertEquals(4, users.getTotalCount());
        Assert.assertArrayEquals(new Long[]{user2, user4}, users.getResults().stream().map(User::getId).toArray());
    }

    @Rollback
    @Test
    public void testGetAllUsersNewest() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, USERS);
        long user1 = insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);
        long user2 = insertUser(USERNAME2, NAME, CREATION_DATE.plusHours(10), "email2", ENABLE);
        long user3 = insertUser("username3", NAME, CREATION_DATE.plusHours(8), "email3", ENABLE);
        long user4 = insertUser("username4", NAME, CREATION_DATE.plusHours(20), "email4", ENABLE);

        PaginatedCollection<User> users = userDao.getAllUsers(UserDao.SortCriteria.NEWEST, PAGE_SECOND, PAGE_SIZE);
        Assert.assertEquals(4, users.getTotalCount());
        Assert.assertArrayEquals(new Long[]{user3, user1}, users.getResults().stream().map(User::getId).toArray());
    }

    @Rollback
    @Test
    public void testGetAllUsersName() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, USERS);
        long user1 = insertUser(USERNAME, "name", CREATION_DATE, EMAIL, ENABLE);
        long user2 = insertUser(USERNAME2, "xname", CREATION_DATE.plusHours(10), "email2", ENABLE);
        long user3 = insertUser("username3", "yname", CREATION_DATE.plusHours(8), "email3", ENABLE);
        long user4 = insertUser("username4", "zname", CREATION_DATE.plusHours(20), "email4", ENABLE);

        PaginatedCollection<User> users = userDao.getAllUsers(UserDao.SortCriteria.USERNAME, PAGE_SECOND, PAGE_SIZE);
        Assert.assertEquals(4, users.getTotalCount());
        Assert.assertArrayEquals(new String[]{"yname", "zname"}, users.getResults().stream().map(User::getName).toArray());
    }

    @Rollback
    @Test
    public void testGetAllUsersHottest() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, USERS);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, POSTS);
        long user1 = insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);
        long post1 = insertPost(user1);
        long user2 = insertUser(USERNAME2, NAME, CREATION_DATE, "email2", ENABLE);
        long post2 = insertPost(user2);
        long user3 = insertUser("username3", NAME, CREATION_DATE.plusHours(8), "email3", ENABLE);
        long user4 = insertUser("username4", NAME, CREATION_DATE.plusHours(20), "email4", ENABLE);

        insertPostLike(post1, user1, UPVOTE);
        insertPostLike(post2, user1, UPVOTE);
        insertPostLike(post1, user2, UPVOTE);
        insertPostLike(post2, user2, UPVOTE);
        insertPostLike(post1, user3, UPVOTE);

        PaginatedCollection<User> users = userDao.getAllUsers(UserDao.SortCriteria.LIKES, PAGE_FIRST, PAGE_SIZE);
        Assert.assertEquals(4, users.getTotalCount());
        Assert.assertArrayEquals(new Long[]{user1, user2}, users.getResults().stream().map(User::getId).toArray());
    }


    @Rollback
    @Test
    public void testSearchUsers() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate ,USERS);

        insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);
        insertUser("usuario", NAME2, CREATION_DATE, "email2", ENABLE);
        insertUser("ussssername3", "test", CREATION_DATE.plusHours(8), "email3", ENABLE);
        insertUser("username4", "test", CREATION_DATE.plusHours(20), "email4", ENABLE);

        PaginatedCollection<User> users = userDao.searchUsers(USERNAME, UserDao.SortCriteria.USERNAME, PAGE_FIRST, PAGE_SIZE);
        Assert.assertEquals(2, users.getTotalCount());
        Assert.assertArrayEquals(new String[]{USERNAME, "username4"}, users.getResults().stream().map(User::getUsername).toArray());
    }

    @Rollback
    @Test
    public void testSearchUsersByRole() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate ,USERS);

        insertUser(USERNAME, NAME, CREATION_DATE, EMAIL, ENABLE);
        insertUser(USERNAME2, NAME, CREATION_DATE, "email2", ENABLE);

        PaginatedCollection<User> users = userDao.searchUsersByRole(USERNAME, Role.ADMIN_ROLE, UserDao.SortCriteria.LIKES, PAGE_FIRST, PAGE_SIZE);
        Assert.assertEquals(0, users.getTotalCount());

        PaginatedCollection<User> users2 = userDao.searchUsersByRole(USERNAME, Role.USER_ROLE, UserDao.SortCriteria.LIKES, PAGE_FIRST, PAGE_SIZE);
        Assert.assertEquals(2, users2.getTotalCount());
    }

    @Test
    public void searchDeletedUsers() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate ,USERS);

        insertUser(USERNAME, "NAME", CREATION_DATE, EMAIL, ENABLE);
        insertUser(USERNAME, "NOmbre", CREATION_DATE, "email2", ENABLE);
        insertUser(USERNAME, NAME, CREATION_DATE, "email3", DISABLE);
        insertUser("USUARIO", "Nein", CREATION_DATE, "email4", DISABLE);

        PaginatedCollection<User> users = userDao.searchDeletedUsers(USERNAME, UserDao.SortCriteria.LIKES, PAGE_FIRST, PAGE_SIZE);
        Assert.assertEquals(1, users.getTotalCount());

    }

    private long insertUser(String username, String name, LocalDateTime creationDate,  String email, boolean enabled){
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, USERS, "username = ?", username);
        HashMap<String, Object> map = new HashMap<>();

        map.put("creation_date", creationDate);
        map.put("username", username);
        map.put("password", PASSWORD);
        map.put("name", name);
        map.put("email", email);
        map.put("description", DESCRIPTION);
        map.put("avatar_id", null);
        map.put("enabled", enabled);

        long id = usersInsert.executeAndReturnKey(map).longValue();

        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("user_id", id);
        map2.put("role_id", USER_ROLE);

        usersRolesInsert.execute(map2);
        return id;
    }

    private long insertPost(long user_id){
        HashMap<String, Object> map = new HashMap<>();

        map.put("creation_date", CREATION_DATE);
        map.put("title", "title");
        map.put("user_id", user_id);
        map.put("category_id", 1);
        map.put("word_count", 1);
        map.put("body", "body");
        map.put("enabled", ENABLE);

        long post_id = postsInsert.executeAndReturnKey(map).longValue();

        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("post_id", post_id);
        map2.put("movie_id", ID);
        postsMoviesInsert.execute(map2);

        return post_id;

    }

    private void insertPostLike(long postId, long userId, int value) {

        Map<String, Object> map = new HashMap<>();
        map.put("post_id", postId);
        map.put("user_id", userId);
        map.put("value", value);
        postsLikesInsert.execute(map);
    }


}
