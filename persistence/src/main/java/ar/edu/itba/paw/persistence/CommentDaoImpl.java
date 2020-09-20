package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class CommentDaoImpl implements CommentDao {

    // Constants with Table Names
    private static final String COMMENTS = TableNames.COMMENTS.getTableName();
    private static final String USERS = TableNames.USERS.getTableName();
    private static final String USER_ROLE = TableNames.USER_ROLE.getTableName();
    private static final String ROLES = TableNames.ROLES.getTableName();

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert commentInsert;


    private static final String SELECT_COMMENTS = "SELECT " +
            COMMENTS + ".comment_id c_comment_id, " +
            "coalesce(" + COMMENTS + ".parent_id, 0) c_parent_id, " +
            COMMENTS + ".post_id c_post_id, " +
            COMMENTS + ".user_email c_user_email, " +
            COMMENTS + ".creation_date c_creation_date, " +
            COMMENTS + ".body c_body, " +

            USERS + ".user_id u_user_id, " +
            USERS + ".creation_date u_creation_date, " +
            USERS + ".username u_username, " +
            USERS + ".password u_password, " +
            USERS + ".name u_name, " +
            USERS + ".email u_email, " +

            "FROM " + COMMENTS +
            " INNER JOIN " + USERS + " ON " + USERS + ".user_id = " + COMMENTS + ".user_id";

    private static final String SELECT_COMMENTS_WITH_CHILDREN = "SELECT " +
            COMMENTS + ".comment_id c_comment_id, " +
            "coalesce(" + COMMENTS + ".parent_id, 0) c_parent_id, " +
            COMMENTS + ".post_id c_post_id, " +
            COMMENTS + ".user_email c_user_email, " +
            COMMENTS + ".creation_date c_creation_date, " +
            COMMENTS + ".body c_body, " +

            USERS + ".user_id u_user_id, " +
            USERS + ".creation_date u_creation_date, " +
            USERS + ".username u_username, " +
            USERS + ".password u_password, " +
            USERS + ".name u_name, " +
            USERS + ".email u_email, " +
            USERS + ".role_id r_role_id, " +
            USERS + ".role r_role " +

            "FROM " + COMMENTS +
            " INNER JOIN ( " +
                "SELECT " +
                USERS + ".user_id, " + USERS + ".creation_date, " + USERS + ".username, " + USERS + ".password, " +
                USERS + ".name, " + USERS + ".email, " + ROLES + ".role_id, " + ROLES + ".role " +
                "FROM " + USERS +
                " INNER JOIN " + USER_ROLE + " ON " + USERS + ".user_id = " + USER_ROLE + ".user_id " +
                "INNER JOIN " + ROLES + " ON " + USER_ROLE + ".role_id = " + ROLES + ".role_id " +
            ") " + USERS + " ON " + USERS + ".user_id = " + COMMENTS + ".user_id";

    // Users come without roles
    private static final RowMapper<Comment> COMMENT_ROW_MAPPER = (rs, rowNum) ->
            new Comment(rs.getLong("comment_id"), rs.getObject("creation_date", LocalDateTime.class),
                    rs.getLong("post_id"), rs.getLong("parent_id"),
                    null, rs.getString("body"),
                    new User(rs.getLong("u_user_id"), rs.getObject("u_creation_date", LocalDateTime.class),
                            rs.getString("u_username"), rs.getString("u_password"),
                            rs.getString("u_name"), rs.getString("u_email"), Collections.emptyList()));

    // Coalesce parent_id = null to parent_id = 0.
    private static final ResultSetExtractor<Collection<Comment>> COMMENT_ROW_MAPPER_WITH_CHILDREN = (rs) -> {
        List<Comment> result = new ArrayList<>();
        Map<Long, Comment> idToCommentMap = new HashMap<>();
        Map<Long, Collection<Comment>> childrenWithoutParentMap = new HashMap<>();
        Map<Long, Role> idToRoleMap = new HashMap<>();

        long comment_id;
        long role_id;
        Comment currentComment;

        while(rs.next()){

            comment_id = rs.getLong("comment_id");
            role_id = rs.getLong("u_role_id");

            // Returns 0 on null
            if(comment_id != 0 && !idToCommentMap.containsKey(comment_id)) {

                currentComment = new Comment(comment_id,
                        rs.getObject("creation_date", LocalDateTime.class),
                        rs.getLong("post_id"), rs.getLong("parent_id"), new ArrayList<>(),
                        rs.getString("body"),
                        new User(rs.getLong("u_user_id"), rs.getObject("u_creation_date", LocalDateTime.class),
                        rs.getString("u_username"), rs.getString("u_password"),
                        rs.getString("u_name"), rs.getString("u_email"), new HashSet<>()));

                idToCommentMap.put(comment_id, currentComment);

                // Incorporate all children that appeared before currentComment
                if(childrenWithoutParentMap.containsKey(comment_id)) {
                    currentComment.getChildren().addAll(childrenWithoutParentMap.get(comment_id));

                    // Mapping is no longer necessary
                    childrenWithoutParentMap.remove(comment_id);
                }

                // Comment is root
                if (currentComment.getParentId() == 0)
                    result.add(currentComment);

                else {
                    // If parent doesn't exist yet
                    if(!idToCommentMap.containsKey(currentComment.getParentId())) {

                        // Initialize Collection inside Map if necessary
                        if(!childrenWithoutParentMap.containsKey(currentComment.getParentId()))
                            childrenWithoutParentMap.put(currentComment.getParentId(), new ArrayList<>());

                        // Add children to Parent Children Buffer
                        childrenWithoutParentMap.get(currentComment.getParentId()).add(currentComment);
                    }

                    // Parent exists -> Add to parent
                    else
                        idToCommentMap.get(currentComment.getParentId()).getChildren().add(currentComment);
                }
            }

            // Role Id not null
            if(role_id != 0) {

                if(!idToRoleMap.containsKey(role_id))
                    idToRoleMap.put(role_id, new Role(role_id, rs.getString("u_role")));

                idToCommentMap.get(comment_id).getUser().getRoles().add(idToRoleMap.get(role_id));
            }
        }

        return result;
    };

    @Autowired
    public CommentDaoImpl(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);

        commentInsert = new SimpleJdbcInsert(ds)
                .withTableName(COMMENTS)
                .usingGeneratedKeyColumns("comment_id");
    }

    @Override
    public long register(long postId, Long parentId, String body, long userId) {

        Objects.requireNonNull(body);
        Objects.requireNonNull(userMail);

        body = body.trim();
        LocalDateTime creationDate = LocalDateTime.now();

        HashMap<String, Object> map = new HashMap<>();
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("post_id", postId);
        map.put("parent_id", parentId);
        map.put("body", body);
        map.put("user_id", userId);

        return commentInsert.executeAndReturnKey(map).longValue();
    }

    private Collection<Comment> findCommentsBy(String queryAfterFrom, Object[] args, boolean withChildren) {

        final String query = (withChildren? SELECT_COMMENTS_WITH_CHILDREN : SELECT_COMMENTS) + " " + queryAfterFrom;

        if(args != null){
            if(withChildren)
                return jdbcTemplate.query(query, args, COMMENT_ROW_MAPPER_WITH_CHILDREN);
            else
                return jdbcTemplate.query(query, args, COMMENT_ROW_MAPPER);
        }
        else {
            if(withChildren)
                return jdbcTemplate.query(query, COMMENT_ROW_MAPPER_WITH_CHILDREN);
            else
                return jdbcTemplate.query(query, COMMENT_ROW_MAPPER);
        }
    }

    private Optional<Comment> findCommentById(long id, boolean withChildren) {
        return findCommentsBy(
                "WHERE " + COMMENTS + ".comment_id = ?", new Object[] { id }, withChildren)
                .stream().findFirst();
    }

    @Override
    public Optional<Comment> findCommentByIdWithChildren(long id) {
        return findCommentById(id, false);
    }

    @Override
    public Optional<Comment> findCommentByIdWithoutChildren(long id) {
        return findCommentById(id, true);
    }

    private Collection<Comment> findCommentsByPostId(long post_id, boolean withChildren) {
        return findCommentsBy(
                "WHERE " + COMMENTS + ".post_id = ?", new Object[] { post_id }, withChildren);
    }

    @Override
    public Collection<Comment> findCommentsByPostIdWithChildren(long post_id) {
        return findCommentsByPostId(post_id, true);
    }

    @Override
    public Collection<Comment> findCommentsByPostIdWithoutChildren(long post_id) {
        return findCommentsByPostId(post_id, false);
    }
}
