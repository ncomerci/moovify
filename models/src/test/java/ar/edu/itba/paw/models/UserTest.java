package ar.edu.itba.paw.models;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class UserTest {

    private static final long DEFAULT_ID = 1L;
    private static final LocalDateTime DEFAULT_CREATION_DATE = LocalDateTime.now();
    private static final String DEFAULT_USERNAME = "test";
    private static final String DEFAULT_PASSWORD = "test";
    private static final String DEFAULT_NAME = "test";
    private static final String DEFAULT_EMAIL = "test";
    private static final String DEFAULT_DESCRIPTION = "test";
    private static final String DEFAULT_LANGUAGE = "en";
    private static final Image DEFAULT_AVATAR = null;
    private static final Set<Role> DEFAULT_ROLES = Collections.emptySet();
    private static final boolean DEFAULT_ENABLED = true;
    private static final Set<PostLike> DEFAULT_POST_LIKES = Collections.emptySet();
    private static final Set<CommentLike> DEFAULT_COMMENT_LIKES = Collections.emptySet();
    private static final Set<Post> DEFAULT_POSTS = Collections.emptySet();
    private static final Set<Comment> DEFAULT_COMMENTS = Collections.emptySet();
    private static final Set<User> DEFAULT_FOLLOWING = Collections.emptySet();
    private static final Set<Post> DEFAULT_FAVOURITE_POSTS = Collections.emptySet();


    @Test
    public void testCalculateTotalLikes() {

        Set<CommentLike> commentLikes = new HashSet<>();
        CommentLike commentLike;

        Set<PostLike> postLikes = new HashSet<>();
        PostLike postLike;

        // Add 40 likes: 20 positive 20 negative (20 post, 20 comment)
        for (int i = 0; i < 10; i++) {

            // Comment Likes
            commentLike = Mockito.mock(CommentLike.class);
            Mockito.when(commentLike.getValue()).thenReturn(1);

            commentLikes.add(commentLike);

            commentLike = Mockito.mock(CommentLike.class);
            Mockito.when(commentLike.getValue()).thenReturn(-1);

            commentLikes.add(commentLike);

            // Post Likes
            postLike = Mockito.mock(PostLike.class);
            Mockito.when(postLike.getValue()).thenReturn(1);

            postLikes.add(postLike);

            postLike = Mockito.mock(PostLike.class);
            Mockito.when(postLike.getValue()).thenReturn(-1);

            postLikes.add(postLike);
        }

        // Add 2 positive comment likes
        commentLike = Mockito.mock(CommentLike.class);
        Mockito.when(commentLike.getValue()).thenReturn(1);

        commentLikes.add(commentLike);

        commentLike = Mockito.mock(CommentLike.class);
        Mockito.when(commentLike.getValue()).thenReturn(1);

        commentLikes.add(commentLike);

        // Add 1 positive post like
        postLike = Mockito.mock(PostLike.class);
        Mockito.when(postLike.getValue()).thenReturn(1);

        postLikes.add(postLike);

        User user = new User(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_NAME,
                DEFAULT_EMAIL, DEFAULT_DESCRIPTION, DEFAULT_LANGUAGE, DEFAULT_AVATAR, DEFAULT_ROLES, DEFAULT_ENABLED,
                postLikes, commentLikes, DEFAULT_POSTS, DEFAULT_COMMENTS, DEFAULT_FOLLOWING,
                DEFAULT_FAVOURITE_POSTS);

        // Exercise
        user.calculateTotalLikes();

        Assert.assertEquals(3L, user.getTotalLikes());
    }
}
