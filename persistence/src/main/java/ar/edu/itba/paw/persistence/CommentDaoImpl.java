package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.models.Comment;
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
    private static final String POSTS = TableNames.POSTS.getTableName();
    private static final String MOVIES = TableNames.MOVIES.getTableName();
    private static final String POST_MOVIE = TableNames.POST_MOVIE.getTableName();
    private static final String COMMENTS = TableNames.COMMENTS.getTableName();

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert commentInsert;


    private static final String SELECT_COMMENTS = "SELECT " +
            COMMENTS + ".comment_id, " +
            "coalesce(" + COMMENTS + ".parent_id, 0) parent_id, " +
            COMMENTS + ".post_id, " +
            COMMENTS + ".user_email, " +
            COMMENTS + ".creation_date, " +
            COMMENTS + ".body " +
            "FROM " + COMMENTS;

    private static final RowMapper<Comment> COMMENT_ROW_MAPPER = (rs, rowNum) ->
            new Comment(rs.getLong("comment_id"), rs.getObject("creation_date", LocalDateTime.class),
                    rs.getLong("post_id"), rs.getLong("parent_id"), null,
                    rs.getString("body"), rs.getString("user_email"));

    // Coalesce parent_id = null to parent_id = 0.
    private static final ResultSetExtractor<Collection<Comment>> COMMENT_ROW_MAPPER_WITH_CHILDREN = (rs) -> {
        List<Comment> result = new ArrayList<>();
        Map<Long, Comment> idToCommentMap = new HashMap<>();
        Map<Long, Collection<Comment>> childrenWithoutParentMap = new HashMap<>();

        long comment_id;
        Comment currentComment;

        while(rs.next()){

            comment_id = rs.getLong("comment_id");

            // Returns 0 on null
            if(comment_id != 0 && !idToCommentMap.containsKey(comment_id)) {

                currentComment = new Comment(comment_id,
                        rs.getObject("creation_date", LocalDateTime.class),
                        rs.getLong("post_id"), rs.getLong("parent_id"), new ArrayList<>(),
                        rs.getString("body"), rs.getString("user_email"));

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
    public Comment register(long postId, Long parentId, String body, String userMail) {

        Objects.requireNonNull(body);
        Objects.requireNonNull(userMail);

        body = body.trim();
        LocalDateTime creationDate = LocalDateTime.now();

        HashMap<String, Object> map = new HashMap<>();
        map.put("creation_date", Timestamp.valueOf(creationDate));
        map.put("post_id", postId);
        map.put("parent_id", parentId);
        map.put("body", body);
        map.put("user_email", userMail);

        final long commentId = commentInsert.executeAndReturnKey(map).longValue();

        return new Comment(commentId, creationDate, postId, parentId, Collections.emptyList(), body, userMail);
    }

    private Collection<Comment> findCommentsBy(String queryAfterFrom, Object[] args, boolean withChildren) {

        final String query = SELECT_COMMENTS + " " + queryAfterFrom;

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

    private Collection<Comment> findCommentsBy(String whereStatement, boolean withChildren) {
        return findCommentsBy(whereStatement, null, withChildren);
    }

    private Collection<Comment> findCommentsBy(boolean withChildren) {
        return findCommentsBy("", null, withChildren);
    }

    @Override
    public Optional<Comment> findCommentById(long id, boolean withChildren){
        return findCommentsBy(
                "WHERE " + COMMENTS + ".comment_id = ?", new Object[] { id }, withChildren)
                .stream().findFirst();
    }

    @Override
    public Collection<Comment> findCommentsByPostId(long post_id, boolean withChildren){
        return findCommentsBy(
                "WHERE " + COMMENTS + ".post_id = ?", new Object[] { post_id }, withChildren);
    }
}
