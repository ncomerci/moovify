package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class InsertHelper {

    public static final long POST1_ID = 1L;
    public static final long POST2_ID = 2L;
    public static final long POST3_ID = 3L;

    public static final long USER1_ID = 1L;
    public static final long USER2_ID = 2L;
    public static final long USER3_ID = 3L;

    private static final String USER_DESCRIPTION = "In depth description!";
    private static final String USER_LANGUAGE = "en";
    private static final String USER_PASSWORD = "$2a$10$O6SNpY56M8b33xKMe92tEOkEezVsln0ocrREUkCK.OC1JY7G1Nfsm";

    private static final LocalDateTime POST_CREATION_DATE = LocalDateTime.of(2020, 8, 6, 12, 24);
    private static final boolean POST_ENABLE = true;

    private static long userIdCount = 0;
    private static long postIdCount = 0;
    private static long postLikeIdCount = 0;
    private static long commentIdCount = 0;
    private static long commentLikesIdCount = 0;
    private static long movieIdCount = 0;

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert commentInsert;
    private final SimpleJdbcInsert commentLikeInsert;
    private final SimpleJdbcInsert movieJdbcInsert;
    private final SimpleJdbcInsert movieToMovieInsert;
    private final SimpleJdbcInsert usersInsert;
    private final SimpleJdbcInsert usersRolesInsert;
    private final SimpleJdbcInsert postsLikesInsert;
    private final SimpleJdbcInsert postsMoviesInsert;
    private final SimpleJdbcInsert postsInsert;
    private final SimpleJdbcInsert favoritePostsInsert;
    private final SimpleJdbcInsert userFollowInsert;

    public InsertHelper(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;

        this.usersInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(User.TABLE_NAME);

        this.usersRolesInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(User.USER_ROLE_TABLE_NAME);

        this.postsLikesInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(PostVote.TABLE_NAME);

        this.postsMoviesInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(Post.POST_MOVIE_TABLE_NAME);

        this.postsInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(Post.TABLE_NAME);

        this.commentInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(Comment.TABLE_NAME);

        this.commentLikeInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(CommentVote.TABLE_NAME);

        this.movieJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(Movie.TABLE_NAME);

        this.movieToMovieInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(Movie.MOVIE_TO_MOVIE_CATEGORY_TABLE_NAME);

        this.favoritePostsInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(User.USER_FAV_POST);

        this.userFollowInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(User.USERS_FOLLOWS);

    }

    public long insertUser(String username, String name, LocalDateTime creationDate, String email, boolean enabled, String role){

        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, User.TABLE_NAME, "username = ?", username);

        HashMap<String, Object> user = new HashMap<>();

        long id = ++userIdCount;

        user.put("user_id", id);
        user.put("creation_date", creationDate);
        user.put("description", USER_DESCRIPTION);
        user.put("email", email);
        user.put("enabled", enabled);
        user.put("language", USER_LANGUAGE);
        user.put("name", name);
        user.put("password", USER_PASSWORD);
        user.put("username", username);
        user.put("avatar_id", null);

        usersInsert.execute(user);

        HashMap<String, Object> roleMap = new HashMap<>();
        roleMap.put("user_id", id);
        roleMap.put("role_name", role);

        usersRolesInsert.execute(roleMap);

        return id;
    }

    public long insertPost(long user_id){
        return insertPost("title", user_id, POST_CREATION_DATE, 1, 1, "body", POST_ENABLE);

    }

    public void insertPostLike(long postId, long userId, int value) {

        long id = ++postLikeIdCount;

        Map<String, Object> map = new HashMap<>();
        map.put("post_likes_id", id);
        map.put("post_id", postId);
        map.put("user_id", userId);
        map.put("value", value);
        postsLikesInsert.execute(map);
    }

    public long insertComment(boolean enabled, Long parentId, long postId, long userId, String body){

        long id = ++commentIdCount;

        Map<String, Object> comment = new HashMap<>();

        comment.put("comment_id", id);
        comment.put("body", body);
        comment.put("creation_date", Timestamp.valueOf(LocalDateTime.of(2020, 8, 6, 12, 14).plusHours(id)));
        comment.put("edited", false);
        comment.put("enabled", enabled);
        comment.put("last_edited", null);
        comment.put("parent_id", parentId);
        comment.put("post_id", postId);
        comment.put("user_id", userId);

        commentInsert.execute(comment);

        return id;
    }

    public void insertCommentLike(int value, long commentId, long userId){

        long id = ++commentLikesIdCount;

        Map<String, Object> map = new HashMap<>();

        map.put("comments_likes_id", id);
        map.put("value", value);
        map.put("user_id", userId);
        map.put("comment_id", commentId);
        commentLikeInsert.execute(map);
    }

    public long insertMovie(String title, long tmdbId, String imdbId, LocalDate releaseDate) {

        Map<String, Object> map = new HashMap<>();

        final long id = ++movieIdCount;

        map.put("movie_id", id);
        map.put("creation_date", Timestamp.valueOf(LocalDateTime.of(2020,8,6,12,16)));
        map.put("release_date", releaseDate);
        map.put("title", title);
        map.put("original_title", "");
        map.put("tmdb_id", tmdbId);
        map.put("imdb_id", imdbId);
        map.put("original_language", "");
        map.put("overview", "");
        map.put("popularity", 5.2);
        map.put("runtime", 5.2);
        map.put("vote_average", 5.2);

        movieJdbcInsert.execute(map);

        return id;
    }

    public void insertMovieToMovieCategory(int movie_id, long category_id) {

        Map<String, Object> map = new HashMap<>();

        map.put("tmdb_category_id", category_id);
        map.put("tmdb_id", movie_id);

        movieToMovieInsert.execute(map);
    }

    public void insertPostMovie(long postId, long movieId) {

        Map<String, Object> map = new HashMap<>();
        map.put("post_id", postId);
        map.put("movie_id", movieId);

        postsMoviesInsert.execute(map);
    }

    public void insertFavoritePost(long postId, long userId) {

        Map<String, Object> map = new HashMap<>();
        map.put("post_id", postId);
        map.put("user_id", userId);

        favoritePostsInsert.execute(map);
    }

    public void insertFollowingUser(long followId, long userId) {

        Map<String, Object> map = new HashMap<>();
        map.put("user_follow_id", followId);
        map.put("user_id", userId);

        userFollowInsert.execute(map);
    }

    public long insertPost(String title, long userId, LocalDateTime creationDate, long categoryId, int wordCount, String body, boolean enable) {

        final long postId = ++postIdCount;

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("post_id", postId);
        postMap.put("body", body);
        postMap.put("creation_date", Timestamp.valueOf(creationDate));
        postMap.put("edited", false);
        postMap.put("enabled", enable);
        postMap.put("last_edited", null);
        postMap.put("title", title);
        postMap.put("word_count", wordCount);
        postMap.put("category_id", categoryId);
        postMap.put("user_id", userId);

        postsInsert.execute(postMap);

        return postId;

    }
}