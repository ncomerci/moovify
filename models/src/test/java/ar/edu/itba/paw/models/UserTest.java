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
    private static final Set<PostVote> DEFAULT_POST_LIKES = Collections.emptySet();
    private static final Set<CommentVote> DEFAULT_COMMENT_LIKES = Collections.emptySet();
    private static final Set<Post> DEFAULT_POSTS = Collections.emptySet();
    private static final Set<Comment> DEFAULT_COMMENTS = Collections.emptySet();
    private static final Set<User> DEFAULT_FOLLOWING = Collections.emptySet();
    private static final Set<User> DEFAULT_FOLLOWERS = Collections.emptySet();
    private static final Set<Post> DEFAULT_FAVOURITE_POSTS = Collections.emptySet();

    @Test
    public void testCalculateTotalLikes() {

        Set<Comment> comments = new HashSet<>();
        Comment comment;

        Set<Post> posts = new HashSet<>();
        Post post;

        // Add 50 likes: 350 positive 300 negative
        for (int i = 0; i < 10; i++) {

            // Comment Likes
            comment = Mockito.mock(Comment.class);
            Mockito.when(comment.getTotalVotes()).thenReturn(15L);

            comments.add(comment);

            comment = Mockito.mock(Comment.class);
            Mockito.when(comment.getTotalVotes()).thenReturn(-12L);

            comments.add(comment);

            // Post Likes
            post = Mockito.mock(Post.class);
            Mockito.when(post.getTotalVotes()).thenReturn(20L);

            posts.add(post);

            post = Mockito.mock(Post.class);
            Mockito.when(post.getTotalVotes()).thenReturn(-18L);

            posts.add(post);
        }

        // Add 50 positive comment likes
        comment = Mockito.mock(Comment.class);
        Mockito.when(comment.getTotalVotes()).thenReturn(50L);

        comments.add(comment);

        // Add 50 negative post like
        post = Mockito.mock(Post.class);
        Mockito.when(post.getTotalVotes()).thenReturn(-50L);

        posts.add(post);

        User user = new User(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_NAME,
                DEFAULT_EMAIL, DEFAULT_DESCRIPTION, DEFAULT_LANGUAGE, DEFAULT_AVATAR, DEFAULT_ROLES, DEFAULT_ENABLED,
                DEFAULT_POST_LIKES, DEFAULT_COMMENT_LIKES, posts, comments, DEFAULT_FOLLOWING, DEFAULT_FOLLOWERS,
                DEFAULT_FAVOURITE_POSTS);

        // Exercise
        user.calculateTotalLikes();

        Assert.assertEquals(50L, user.getTotalLikes());
    }
}
