package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.RoleDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateEmailException;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUsernameException;
import ar.edu.itba.paw.interfaces.persistence.exceptions.InvalidPaginationArgumentException;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.exceptions.UserRegistrationWithoutRoleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

    private static final String USERS = TableNames.USERS.getTableName();
    private static final String ROLES = TableNames.ROLES.getTableName();
    private static final String USER_ROLE = TableNames.USER_ROLE.getTableName();
    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String POSTS_LIKES = TableNames.POSTS_LIKES.getTableName();
    private static final String COMMENTS_LIKES = TableNames.COMMENTS_LIKES.getTableName();
    private static final String COMMENTS = TableNames.COMMENTS.getTableName();

    private static final String BASE_USER_SELECT = "SELECT " +
            USERS + ".user_id u_user_id, " +
            USERS + ".creation_date u_creation_date, " +
            USERS + ".username u_username, " +
            USERS + ".password u_password, " +
            USERS + ".name u_name, " +
            USERS + ".email u_email, " +
            USERS + ".description u_description, " +
            USERS + ".avatar_id u_avatar_id, " +
            USERS + ".enabled u_enabled";

    private static final String TOTAL_LIKES_SELECT = "TOTAL_LIKES.total_likes u_total_likes";

    private static final String ROLE_SELECT =
            ROLES + ".role_id r_role_id, " +
            ROLES + ".role r_role";


    private static final String BASE_USER_FROM = "FROM " + USERS;

    private static final String TOTAL_LIKES_FROM = "INNER JOIN ( " +
            "SELECT " + USERS + ".user_id, coalesce(post_likes.total_likes, 0) + coalesce(comment_likes.total_likes, 0) total_likes " +
                    "FROM " + USERS +
                        " LEFT OUTER JOIN ( " +
                            "SELECT " + POSTS + ".user_id, SUM(" + POSTS_LIKES + ".value) total_likes " +
                            "FROM " + POSTS +
                                " INNER JOIN " + POSTS_LIKES + " ON " + POSTS + ".post_id = " + POSTS_LIKES + ".post_id " +
                            "GROUP BY " + POSTS + ".user_id " +
                        ") post_likes ON " + USERS + ".user_id = post_likes.user_id " +

                        "LEFT OUTER JOIN ( " +
                            "SELECT " + COMMENTS + ".user_id, SUM(" + COMMENTS_LIKES + ".value) total_likes " +
                            "FROM " + COMMENTS +
                                " INNER JOIN " + COMMENTS_LIKES + " ON " + COMMENTS + ".comment_id = " + COMMENTS_LIKES + ".comment_id " +
                            "GROUP BY " + COMMENTS + ".user_id " +
                    ") comment_likes ON " + USERS + ".user_id = comment_likes.user_id " +
            ") TOTAL_LIKES ON TOTAL_LIKES.user_id = " + USERS + ".user_id";

    private static final String ROLE_FROM =
            "INNER JOIN " + USER_ROLE + " ON " + USERS + ".user_id = " + USER_ROLE + ".user_id " +
            "INNER JOIN " + ROLES + " ON " + USER_ROLE + ".role_id = " + ROLES + ".role_id";


    private static final ResultSetExtractor<Collection<User>> USER_ROW_MAPPER = (rs) -> {
        Map<Long, User> idToUserMap = new LinkedHashMap<>();
        Map<Long, Role> idToRoleMap = new HashMap<>();
        long userId;
        long role_id;

        while(rs.next()){

            userId = rs.getLong("u_user_id");

            if(!idToUserMap.containsKey(userId)) {
                idToUserMap.put(userId,
                        new User(userId, rs.getObject("u_creation_date", LocalDateTime.class),
                                rs.getString("u_username"), rs.getString("u_password"),
                                rs.getString("u_name"), rs.getString("u_email"), rs.getString("u_description"),
                                rs.getLong("u_avatar_id"), rs.getLong("u_total_likes"),
                                new HashSet<>(), rs.getBoolean("u_enabled"))
                );
            }

            role_id = rs.getLong("r_role_id");

            if(!idToRoleMap.containsKey(role_id))
                idToRoleMap.put(role_id, new Role(role_id, rs.getString("r_role")));

            idToUserMap.get(userId).getRoles().add(idToRoleMap.get(role_id));

        }

        return idToUserMap.values();
    };

    private static final EnumMap<SortCriteria,String> sortCriteriaQueryMap = initializeSortCriteriaQuery();

    private static EnumMap<SortCriteria, String> initializeSortCriteriaQuery() {

        EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, USERS + ".creation_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, USERS + ".creation_date");
        sortCriteriaQuery.put(SortCriteria.LIKES, "TOTAL_LIKES.total_likes desc");
        sortCriteriaQuery.put(SortCriteria.USERNAME, USERS + ".username");

        return sortCriteriaQuery;
    }

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcUserInsert;
    private final SimpleJdbcInsert jdbcUserRoleInsert;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    public UserDaoImpl(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);

        jdbcUserInsert = new SimpleJdbcInsert(ds)
                .withTableName(USERS)
                .usingGeneratedKeyColumns("user_id");

        jdbcUserRoleInsert = new SimpleJdbcInsert(ds)
                .withTableName(USER_ROLE);
    }

    @Override
    public User register(String username, String password, String name, String email, String description, Collection<String> roleNames, Long avatarId, boolean enabled) throws DuplicateEmailException, DuplicateUsernameException {

        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        Objects.requireNonNull(name);
        Objects.requireNonNull(email);
        Objects.requireNonNull(description);
        Objects.requireNonNull(roleNames);

        final LocalDateTime creationDate = LocalDateTime.now();

        // TODO: Se puede evitar esta query. Importante! Requiere un insert mas complejo.
        final Collection<Role> roles = roleDao.findRolesByName(roleNames);

        if(roles == null || roles.isEmpty()) {
            LOGGER.error("Tried to create User {} without valid roles (roles used: {})", username, roles);
            throw new UserRegistrationWithoutRoleException();
        }

        HashMap<String, Object> map = new HashMap<>();

        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("username", username);
        map.put("password", password);
        map.put("name", name);
        map.put("email", email);
        map.put("description", description);
        map.put("avatar_id", avatarId);
        map.put("enabled", enabled);

        final long userId;

        try {
           userId = jdbcUserInsert.executeAndReturnKey(map).longValue();
        }
        catch(DuplicateKeyException e) {
            final String errorMessage = e.getMessage();

            if(errorMessage.contains("users_username_key")) {
                LOGGER.error("Tried to create User with duplicated username {}", username);
                throw new DuplicateUsernameException();
            }

            else if(errorMessage.contains("users_email_key")) {
                LOGGER.error("Tried to create User with duplicated email {}", email);
                throw new DuplicateEmailException();
            }

            else
                throw e;
        }

        for(Role role: roles){
            map = new HashMap<>();
            map.put("user_id", userId);
            map.put("role_id", role.getId());
            jdbcUserRoleInsert.execute(map);
        }

        final User user = new User(userId, creationDate, username, password, name, email, description, avatarId, 0, roles, true);

        LOGGER.debug("Created user {}", user);

        return user;
    }

    @Override
    public void updateName(User user, String name) {

        jdbcTemplate.update("UPDATE " + USERS + " SET  name = ? WHERE user_id = ?", name, user.getId());

        LOGGER.info("Updated name from User {} from {} to {}", user.getId(), user.getName(), name);
    }

    @Override
    public void updateUsername(User user, String username) throws DuplicateUsernameException {
        try {
            jdbcTemplate.update("UPDATE " + USERS + " SET  username = ? WHERE user_id = ?", username, user.getId());
        }
        catch(DuplicateKeyException e) {
            LOGGER.error("Tried to change User's {} username to {}, which already exists", user.getId(), username);
            throw new DuplicateUsernameException();
        }

        LOGGER.info("Updated username from User {} from {} to {}", user.getId(), user.getUsername(), username);
    }

    @Override
    public void updateDescription(User user, String description) {

        jdbcTemplate.update("UPDATE " + USERS + " SET  description = ? WHERE user_id = ?", description, user.getId());

        LOGGER.info("Updated description from User {} to {}", user.getId(), description);
    }

    @Override
    public void updatePassword(User user, String password) {

        jdbcTemplate.update("UPDATE " + USERS + " SET password = ? WHERE user_id = ?", password, user.getId());

        LOGGER.info("Updated User's {} password to {}", user.getId(), password);
    }

    @Override
    public void updateAvatarId(User user, long avatarId) {

        jdbcTemplate.update("UPDATE " + USERS + " SET avatar_id = ? WHERE user_id = ?", avatarId, user.getId());

        LOGGER.info("Updated User's {} avatar id to {}", user.getId(), avatarId);
    }

    @Override
    public void deleteUser(User user) {

        jdbcTemplate.update("UPDATE " + USERS + " SET enabled = false WHERE user_id = ?", user.getId());

        LOGGER.info("Disabled User {}", user.getId());
    }

    @Override
    public void restoreUser(User user) {

        jdbcTemplate.update("UPDATE " + USERS + " SET enabled = true WHERE user_id = ?", user.getId());

        LOGGER.info("Restored User {}", user.getId());
    }

    // TODO: can be done in a single query. Important!
    @Override
    public Collection<Role> addRoles(User user, Collection<String> roleNames) {
        final Collection<Role> roles = roleDao.findRolesByName(roleNames);

        HashMap<String, Object> map;

        for (Role role: roles) {
            map = new HashMap<>();

            map.put("user_id", user.getId());
            map.put("role_id", role.getId());

            jdbcUserRoleInsert.execute(map);
        }

        LOGGER.info("Added Roles {} to User {}", roleNames, user.getId());

        return roles;
    }

    @Override
    public int hasUserLiked(User user, Post post) {

        final int result = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(" + POSTS_LIKES + ".value), 0)  FROM " + POSTS_LIKES +
                " WHERE user_id = ? AND post_id = ?", new Object[]{ user.getId(), post.getId() }, Integer.class);

        LOGGER.info("Checked if User {} had liked Post {}", user.getId(), post.getId());

        return result;
    }

    @Override
    public void replaceUserRole(final User user, final String newRole, final String oldRole) {

        jdbcTemplate.update("UPDATE " + USER_ROLE +
                " SET role_id = (SELECT role_id FROM " + ROLES + " WHERE role = ?)" +
                " WHERE " + USER_ROLE + ".user_id = ?" +
                "  AND " + USER_ROLE + ".role_id = " +
                "(SELECT role_id FROM " + ROLES + " WHERE role = ?)", newRole, user.getId(), oldRole);

        LOGGER.info("Replaced User {} Role from {} to {}", user.getId(), oldRole, newRole);
    }

    private Collection<User> executeQuery(String select, String from, String where, String orderBy, Object[] args) {

        final String query = String.format("%s %s %s %s", select, from, where, orderBy);

        LOGGER.debug("Query executed in UserDaoImpl : {}. Args: {}", query, args);

        if(args != null)
            return jdbcTemplate.query(query, args, USER_ROW_MAPPER);

        else
            return jdbcTemplate.query(query, USER_ROW_MAPPER);
    }

    private Collection<User> buildAndExecuteQuery(String customWhereStatement, Object[] args){

        final String select = buildSelectStatement();

        final String from = buildFromStatement();

        final Collection<User> result =  executeQuery(select, from, customWhereStatement, "", args);

        LOGGER.debug("Not paginated query executed for {} in UserDaoImpl with result {}", customWhereStatement, result);

        return result;
    }

    private PaginatedCollection<User> buildAndExecutePaginatedQuery(String customWhereStatement, SortCriteria sortCriteria, int pageNumber, int pageSize, Object[] args) {

        final String select = buildSelectStatement();

        final String from = buildFromStatement();

        final String totalUserCountQuery = String.format(
                "SELECT COUNT(DISTINCT " + USERS + ".user_id) %s %s", from, customWhereStatement);

        // Execute original query to count total posts in the query
        final int totalUserCount = jdbcTemplate.queryForObject(
                totalUserCountQuery, args, Integer.class);

        final String orderBy = buildOrderByStatement(sortCriteria);

        final String pagination = buildLimitAndOffsetStatement(pageNumber, pageSize);

        final String newWhere = String.format(
                "WHERE " + USERS + ".user_id IN ( " +
                        "SELECT AUX.user_id " +
                        "FROM (" +
                            "SELECT ROW_NUMBER() OVER() row_num, INNER_AUX.user_id " +
                            "FROM ( " +
                                "SELECT " + USERS + ".user_id " +
                                "%s %s %s ) INNER_AUX " +
                            ") AUX " +
                        "GROUP BY AUX.user_id " +
                        "ORDER BY MIN(AUX.row_num) " +
                        " %s )", from, customWhereStatement, orderBy, pagination);

        final Collection<User> results = executeQuery(select, from, newWhere, orderBy, args);

        final PaginatedCollection<User> userPaginatedCollection = new PaginatedCollection<>(results, pageNumber, pageSize, totalUserCount);

        LOGGER.debug("Paginated query executed in PostDaoImpl with result {}", userPaginatedCollection);

        return userPaginatedCollection;
    }

    private String buildSelectStatement() {
        return BASE_USER_SELECT + ", " + TOTAL_LIKES_SELECT + ", " + ROLE_SELECT;
    }

    private String buildFromStatement() {
        return BASE_USER_FROM + " " + TOTAL_LIKES_FROM + " " + ROLE_FROM;
    }

    private String buildOrderByStatement(SortCriteria sortCriteria) {

        if(!sortCriteriaQueryMap.containsKey(sortCriteria)) {
            LOGGER.error("SortCriteria implementation not found for {} in UserDaoImpl", sortCriteria);
            throw new IllegalArgumentException();
        }

        return "ORDER BY " + sortCriteriaQueryMap.get(sortCriteria);
    }

    private String buildLimitAndOffsetStatement(int pageNumber, int pageSize) {

        if(pageNumber < 0 || pageSize <= 0) {
            LOGGER.error("Invalid pagination argument found in UserDaoImpl. pageSize: {}, pageNumber: {}", pageSize, pageNumber);
            throw new InvalidPaginationArgumentException();
        }

        return String.format("LIMIT %d OFFSET %d", pageSize, pageNumber * pageSize);
    }

    private static final String ENABLED_FILTER = USERS + ".enabled = true";

    @Override
    public Optional<User> findUserById(long id) {

        LOGGER.info("Find User By Id {}", id);
        return buildAndExecuteQuery("WHERE " + USERS + ".user_id = ? AND " + ENABLED_FILTER,
                new Object[]{ id }).stream().findFirst();
    }

    @Override
    public Optional<User> findDeletedUserById(long id) {

        LOGGER.info("Find Deleted User By Id {}", id);
        return buildAndExecuteQuery("WHERE " + USERS + ".user_id = ? AND " + USERS + ".enabled = false",
                new Object[]{ id }).stream().findFirst();
    }

    @Override
    public Optional<User> findUserByUsername(String username) {

        LOGGER.info("Find User By Username {}", username);
        return buildAndExecuteQuery("WHERE " + USERS + ".username = ? AND " + ENABLED_FILTER,
                new Object[]{ username }).stream().findFirst();
    }

    @Override
    public Optional<User> findUserByEmail(String email) {

        LOGGER.info("Find User By Email {}", email);
        return buildAndExecuteQuery("WHERE " + USERS + ".email = ? AND " + ENABLED_FILTER,
                new Object[]{ email }).stream().findFirst();
    }

    @Override
    public PaginatedCollection<User> getAllUsers(SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Get All Users Order By {}. Page number {}, Page Size {}", sortCriteria, pageNumber, pageSize);
        return buildAndExecutePaginatedQuery(
                "WHERE " + ENABLED_FILTER, sortCriteria, pageNumber, pageSize, null);
    }

    // Search Query Statements
    private static final String SEARCH_BY_USERNAME = "LOWER(" + USERS + ".username) LIKE '%' || LOWER(?) || '%'";

    private static final String SEARCH_BY_ROLE = USERS + ".user_id IN ( " +
            "SELECT " + USER_ROLE + ".user_id " +
            "FROM " + ROLES + " " +
                "INNER JOIN " + USER_ROLE + " ON " + ROLES + ".role_id = " + USER_ROLE + ".role_id " +
            "WHERE LOWER(" + ROLES + ".role) = LOWER(?))";

    @Override
    public PaginatedCollection<User> searchUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Users By Name {} Order By {}. Page number {}, Page Size {}", query, sortCriteria, pageNumber, pageSize);
        return buildAndExecutePaginatedQuery(
                "WHERE " + SEARCH_BY_USERNAME +
                                    " AND " + ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ query });
    }

    @Override
    public PaginatedCollection<User> searchDeletedUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery("WHERE " + SEARCH_BY_USERNAME +
                                                                " AND " + USERS + ".enabled = false",
                sortCriteria, pageNumber, pageSize, new Object[]{ query });
    }

    @Override
    public PaginatedCollection<User> searchUsersByRole(String query, String role, SortCriteria sortCriteria, int pageNumber, int pageSize) {

        LOGGER.info("Search Users By Name {} And Role {} Order By {}. Page number {}, Page Size {}", query, role, sortCriteria, pageNumber, pageSize);
        return buildAndExecutePaginatedQuery("WHERE " + SEARCH_BY_USERNAME +
                                                                " AND " + SEARCH_BY_ROLE +
                                                                " AND " + ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{ query, role });
    }
}
