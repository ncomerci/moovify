package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ModelHelper {

    private static final long USER_ID = 1L;
    private static final LocalDateTime USER_CREATION_DATE = LocalDateTime.of(2020,8,6,8,6);
    private static final String USER_DESCRIPTION = "In depth description!";
    private static final String USER_EMAIL = "prueba@prueba.com";
    private static final boolean USER_ENABLE = true;
    private static final String USER_LANGUAGE = "en";
    private static final String USER_NAME = "testUser";
    private static final String USER_PASSWORD = "$2a$10$O6SNpY56M8b33xKMe92tEOkEezVsln0ocrREUkCK.OC1JY7G1Nfsm";
    private static final String USER_USERNAME = "'testUsername'";
    private static final Image USER_AVATAR = null;
    private static final Set<Role> USER_ROLES = new HashSet<>();
    private static final Set<PostLike> USER_POST_LIKES = new HashSet<>();
    private static final Set<CommentLike> USER_COMMENT_LIKES = new HashSet<>();
    private static final Set<Post> USER_POSTS = new HashSet<>();
    private static final Set<Comment> USER_COMMENTS = new HashSet<>();
    private static final Set<User> USER_FOLLOWING = new HashSet<>();
    private static final Set<Post> USER_FAVORITE_POSTS = new HashSet<>();


    private static final long INVALID_USER_ID = 100L;
    private static final String INVALID_USER_EMAIL = "user@invalido.com";
    private static final String INVALID_USER_USERNAME = "invalidUser";
    private static final String INVALID_USER_NAME = "invalidUser";


    public static User getHelperUser(){
        return new User(
//                USER_ID,
                USER_CREATION_DATE,
                USER_USERNAME,
                USER_PASSWORD,
                USER_NAME,
                USER_EMAIL,
                USER_DESCRIPTION,
                USER_LANGUAGE,
                USER_AVATAR,
                USER_ROLES,
                USER_ENABLE,
                USER_POST_LIKES,
                USER_COMMENT_LIKES,
                USER_POSTS,
                USER_COMMENTS,
                USER_FOLLOWING,
                USER_FAVORITE_POSTS
                );
    }

    public static User getHelperInvalidUser(){
        return new User(
                INVALID_USER_ID,
                USER_CREATION_DATE,
                INVALID_USER_USERNAME,
                USER_PASSWORD,
                INVALID_USER_NAME,
                INVALID_USER_EMAIL,
                USER_DESCRIPTION,
                USER_LANGUAGE,
                USER_AVATAR,
                USER_ROLES,
                USER_ENABLE,
                USER_POST_LIKES,
                USER_COMMENT_LIKES,
                USER_POSTS,
                USER_COMMENTS,
                USER_FOLLOWING,
                USER_FAVORITE_POSTS
        );
    }


}