package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.RoleDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.exceptions.UserRegistrationWithoutRoleException;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String USERS = TableNames.USERS.getTableName();
    private static final String ROLES = TableNames.ROLES.getTableName();
    private static final String USER_ROLE = TableNames.USER_ROLE.getTableName();
    private static final String POSTS_LIKES = TableNames.POSTS_LIKES.getTableName();
    private static final String COMMENTS_LIKES = TableNames.COMMENTS_LIKES.getTableName();

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

    private static final String ROLE_SELECT =
            ROLES + ".role_id r_role_id, " +
            ROLES + ".role r_role";

    private static final String LIKES_SELECT =
            COMMENTS_LIKES + ".comment_id c_comment_id";


    private static final String BASE_USER_FROM = "FROM " + USERS;

    private static final String ROLE_FROM =
            "INNER JOIN " + USER_ROLE + " ON " + USERS + ".user_id = " + USER_ROLE + ".user_id " +
            "INNER JOIN " + ROLES + " ON " + USER_ROLE + ".role_id = " + ROLES + ".role_id";

    private static final String LIKES_FROM =
            "LEFT OUTER JOIN " + COMMENTS_LIKES + " ON " + COMMENTS_LIKES + ".user_id = " + USERS + ".user_id ";


    private static final ResultSetExtractor<Collection<User>> USER_ROW_MAPPER = (rs) -> {
        Map<Long, User> idToUserMap = new LinkedHashMap<>();
        Map<Long, Role> idToRoleMap = new HashMap<>();
        long userId;
        long role_id;
        long comment_id;

        while(rs.next()){

            userId = rs.getLong("u_user_id");

            if(!idToUserMap.containsKey(userId)) {
                idToUserMap.put(userId,
                        new User(userId, rs.getObject("u_creation_date", LocalDateTime.class),
                                rs.getString("u_username"), rs.getString("u_password"),
                                rs.getString("u_name"), rs.getString("u_email"), rs.getString("u_description"), rs.getLong("u_avatar_id"),
                                new HashSet<>(), rs.getBoolean("u_enabled"), new HashSet<>())
                );
            }

            role_id = rs.getLong("r_role_id");

            if(!idToRoleMap.containsKey(role_id))
                idToRoleMap.put(role_id, new Role(role_id, rs.getString("r_role")));

            idToUserMap.get(userId).getRoles().add(idToRoleMap.get(role_id));

            comment_id = rs.getLong("c_comment_id");

            if(comment_id > 0)
                idToUserMap.get(userId).getLikedComments().add(comment_id);
        }

        return idToUserMap.values();
    };

    private static final EnumMap<SortCriteria,String> sortCriteriaQueryMap = initializeSortCriteriaQuery();

    private static EnumMap<SortCriteria, String> initializeSortCriteriaQuery() {

        EnumMap<SortCriteria, String> sortCriteriaQuery = new EnumMap<>(SortCriteria.class);

        sortCriteriaQuery.put(SortCriteria.NEWEST, USERS + ".creation_date desc");
        sortCriteriaQuery.put(SortCriteria.OLDEST, USERS + ".creation_date");

        return sortCriteriaQuery;
    }

    private static final String ENABLED_FILTER = USERS + ".enabled = true";

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
    public User register(String username, String password, String name, String email, String description, Collection<String> roleNames, Long avatarId, boolean enabled) {

        LocalDateTime creationDate = LocalDateTime.now();

        // TODO: Se puede evitar esta query. Importante! Requiere un insert mas complejo.
        Collection<Role> roles = roleDao.findRolesByName(roleNames);

        if(roles == null || roles.isEmpty())
            throw new UserRegistrationWithoutRoleException();

        HashMap<String, Object> map = new HashMap<>();
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("username", username);
        map.put("password", password);
        map.put("name", name);
        map.put("email", email);
        map.put("description", description);
        map.put("avatar_id", avatarId);
        map.put("enabled", enabled);

        final long userId = jdbcUserInsert.executeAndReturnKey(map).longValue();

        for(Role role: roles){
            map = new HashMap<>();
            map.put("user_id", userId);
            map.put("role_id", role.getId());
            jdbcUserRoleInsert.execute(map);
        }

        return new User(userId, creationDate, username, password, name, email, description, avatarId, roles, true , Collections.emptySet());
    }

    @Override
    public void editName(long user_id, String name) {
        jdbcTemplate.update("UPDATE " + USERS + " SET  name = ? WHERE user_id = ?", name, user_id);
    }

    @Override
    public void editUsername(long user_id, String username) {
        jdbcTemplate.update("UPDATE " + USERS + " SET  username = ? WHERE user_id = ?", username, user_id);
    }

    @Override
    public void editDescription(long user_id, String description) {
        jdbcTemplate.update("UPDATE " + USERS + " SET  description = ? WHERE user_id = ?", description, user_id);
    }


    @Override
    public void updatePassword(long userId, String password) {
        jdbcTemplate.update("UPDATE " + USERS + " SET password = ? WHERE user_id = ?", password, userId);
    }

    @Override
    public void updateAvatarId(long userId, long avatarId) {
        jdbcTemplate.update("UPDATE " + USERS + " SET avatar_id = ? WHERE user_id = ?", avatarId, userId);
    }

    @Override
    public Collection<Role> addRoles(long userId, Collection<String> roleNames) {
        Collection<Role> roles = roleDao.findRolesByName(roleNames);

        HashMap<String, Object> map;

        for (Role role: roles) {
            map = new HashMap<>();
            map.put("user_id", userId);
            map.put("role_id", role.getId());
            jdbcUserRoleInsert.execute(map);
        }

        return roles;
    }

    @Override
    public boolean userHasRole(long userId, String role) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + USER_ROLE +
                        " INNER JOIN " + ROLES + " ON " + ROLES + ".role_id = " + USER_ROLE + ".role_id" +
                        " WHERE user_id = ? AND role = ?", new Object[]{ userId, role}, Integer.class) > 0;
    }

    @Override
    public boolean userHasRole(String email, String role) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + USERS +
                        " INNER JOIN "+ USER_ROLE + " ON " + USERS + ".user_id = " + USER_ROLE + ".user_id" +
                        " INNER JOIN " + ROLES + " ON " + ROLES + ".role_id = " + USER_ROLE + ".role_id" +
                        " WHERE email = ? AND role = ?", new Object[]{ email, role }, Integer.class) > 0;
    }

    @Override
    public int hasUserLiked(long user_id, long post_id) {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE( SUM(" + POSTS_LIKES + ".value ),0)  FROM " + POSTS_LIKES +
                " WHERE user_id = ? AND post_id = ?", new Object[] { user_id, post_id }, Integer.class);
    }

    @Override
    public void replaceUserRole(final long userId, final String newRole, final String oldRole) {

        jdbcTemplate.update("UPDATE " + USER_ROLE +
                " SET role_id = (SELECT role_id FROM " + ROLES + " WHERE role = ?)" +
                "FROM " + ROLES +
                " WHERE " + USER_ROLE + ".user_id = ?" +
                "  AND " + ROLES + ".role_id = " + USER_ROLE + ".role_id" +
                "  AND " + ROLES + ".role = ?", newRole, userId, oldRole);
    }

    private Collection<User> executeQuery(String select, String from, String where, String orderBy, Object[] args) {

        final String query = select + " " + from + " " + where + " " + orderBy;

        if(args != null)
            return jdbcTemplate.query(query, args, USER_ROW_MAPPER);

        else
            return jdbcTemplate.query(query, USER_ROW_MAPPER);
    }

    private Collection<User> buildAndExecuteQuery(String customWhereStatement, Object[] args){

        final String select = BASE_USER_SELECT + ", " + ROLE_SELECT + ", " + LIKES_SELECT;

        final String from = BASE_USER_FROM + " " + ROLE_FROM + " " + LIKES_FROM;

        return executeQuery(select, from, customWhereStatement, "", args);
    }

    private PaginatedCollection<User> buildAndExecutePaginatedQuery(String customWhereStatement, SortCriteria sortCriteria, int pageNumber, int pageSize, Object[] args) {

        final String select = BASE_USER_SELECT + ", " + ROLE_SELECT + ", " + LIKES_SELECT;

        final String from = BASE_USER_FROM + " " + ROLE_FROM + " " + LIKES_FROM;

        // Execute original query to count total posts in the query
        final int totalUserCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT " + USERS + ".user_id) " + from + " " + customWhereStatement, args, Integer.class);

        final String orderBy = buildOrderByStatement(sortCriteria);

        final String pagination = buildLimitAndOffsetStatement(pageNumber, pageSize);

        final String newWhere = "WHERE " + USERS + ".user_id IN (SELECT " + USERS + ".user_id FROM " + USERS + " WHERE " + USERS + ".user_id IN (" +
                "SELECT " + USERS + ".user_id " + from + " " + customWhereStatement +
                " ) " + orderBy + " " + pagination + ")";

        final Collection<User> results = executeQuery(select, from, newWhere, orderBy, args);

        return new PaginatedCollection<>(results, pageNumber, pageSize, totalUserCount);
    }

    private String buildOrderByStatement(SortCriteria sortCriteria) {

        if(!sortCriteriaQueryMap.containsKey(sortCriteria))
            throw new IllegalArgumentException("SortCriteria implementation not found for " + sortCriteria + " in UserDaoImpl.");

        return "ORDER BY " + sortCriteriaQueryMap.get(sortCriteria);
    }

    private String buildLimitAndOffsetStatement(int pageNumber, int pageSize) {

        if(pageNumber < 0 || pageSize <= 0)
            throw new IllegalArgumentException("Illegal User pagination arguments. Page Number: " + pageNumber + ". Page Size: " + pageSize);

        return "LIMIT " + pageSize + " OFFSET " + (pageNumber * pageSize);
    }

    // TODO: CHECK USERS ENABLED. findById, searchUsers
//    " WHERE " + USERS + ".user_id = ?" + " AND "+ USERS + ".enabled = true"
//    WHERE " + USERS + ".username ILIKE '%' || ? || '%'" + " AND "+ USERS + ".enabled = true

    @Override
    public Optional<User> findById(long id) {
        return buildAndExecuteQuery("WHERE " + USERS + ".user_id = ? AND " + ENABLED_FILTER,
                new Object[]{ id }).stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return buildAndExecuteQuery("WHERE " + USERS + ".username = ? AND " + ENABLED_FILTER,
                new Object[]{ username }).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return buildAndExecuteQuery("WHERE " + USERS + ".email = ? AND " + ENABLED_FILTER,
                new Object[]{ email }).stream().findFirst();
    }

    @Override
    public PaginatedCollection<User> searchUsers(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery("WHERE " + USERS + ".username ILIKE '%' || ? || '%' AND " + ENABLED_FILTER,
                sortCriteria, pageNumber, pageSize, new Object[]{query});
    }

    @Override
    public PaginatedCollection<User> getAllUsers(SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return buildAndExecutePaginatedQuery(
                "WHERE " + ENABLED_FILTER, sortCriteria, pageNumber, pageSize, null);
    }
}
