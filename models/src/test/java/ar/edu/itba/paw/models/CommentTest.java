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
public class CommentTest {

    private static final long DEFAULT_ID = 1L;
    private static final LocalDateTime DEFAULT_CREATION_DATE = LocalDateTime.now();
    private static final Post DEFAULT_POST = new Post();
    private static final Comment DEFAULT_PARENT = new Comment();
    private static final Set<Comment> DEFAULT_CHILDREN = Collections.emptySet();
    private static final String DEFAULT_BODY = "";
    private static final boolean DEFAULT_EDITED = false;
    private static final LocalDateTime DEFAULT_LAST_EDITED = null;
    private static final User DEFAULT_USER = new User();
    private static final boolean DEFAULT_ENABLED = true;
    private static final Set<CommentLike> DEFAULT_LIKES = Collections.emptySet();



    @Test
    public void testCalculateTotalLikes() {

            Set<CommentLike> likes = new HashSet<>();
            CommentLike like;

            // Add 20 likes: 10 positive 10 negative
            for (int i = 0; i < 10; i++) {

                like = Mockito.mock(CommentLike.class);
                Mockito.when(like.getValue()).thenReturn(1);

                likes.add(like);

                like = Mockito.mock(CommentLike.class);
                Mockito.when(like.getValue()).thenReturn(-1);

                likes.add(like);
            }

            // Add 2 positive likes
            like = Mockito.mock(CommentLike.class);
            Mockito.when(like.getValue()).thenReturn(1);

            likes.add(like);

            like = Mockito.mock(CommentLike.class);
            Mockito.when(like.getValue()).thenReturn(1);

            likes.add(like);

            // Create comment with likes
            Comment comment = new Comment(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                    DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, likes);

            // Exercise
            comment.calculateTotalLikes();

            Assert.assertEquals(2L, comment.getTotalLikes());
    }

    @Test
    public void testGetLikeValue() {

        long userRealId = 5L;
        int likeValue = -1;

        Set<CommentLike> likes = new HashSet<>();
        CommentLike like;
        User realUser;

        // Create real user with valid value
        realUser = Mockito.mock(User.class);
        Mockito.when(realUser.getId()).thenReturn(userRealId);

        User fakeUser1 = Mockito.mock(User.class);
        Mockito.lenient().when(fakeUser1.getId()).thenReturn(userRealId + 5L);

        User fakeUser2 = Mockito.mock(User.class);
        Mockito.lenient().when(fakeUser2.getId()).thenReturn(userRealId + 10L);

        like = Mockito.mock(CommentLike.class);
        Mockito.when(like.getValue()).thenReturn(likeValue);
        Mockito.when(like.getUser()).thenReturn(realUser);

        likes.add(like);

        // Create other likes
        like = Mockito.mock(CommentLike.class);
        Mockito.lenient().when(like.getUser()).thenReturn(fakeUser1);
        likes.add(like);

        like = Mockito.mock(CommentLike.class);
        Mockito.lenient().when(like.getUser()).thenReturn(fakeUser2);
        likes.add(like);

        Comment comment = new Comment(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, likes);

        // Exercise
        int value = comment.getLikeValue(realUser);

        Assert.assertEquals(likeValue, value);
    }

    @Test
    public void testGetDescendantCount() {


        Set<Comment> descendants1 = new HashSet<>();
        Set<Comment> descendants2 = new HashSet<>();


        Comment descendant1 = new Comment(20L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        Comment descendant2 = new Comment(30L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        Comment descendant3 = new Comment(40L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        Comment descendant4 = new Comment(50L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        Comment descendant5 = new Comment(60L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        descendants1.add(descendant1);
        descendants1.add(descendant2);
        descendants1.add(descendant3);

        descendants2.add(descendant4);
        descendants2.add(descendant5);

        Set<Comment> children = new HashSet<>();

        Comment child1 = new Comment(2L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, descendants1,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        Comment child2 = new Comment(3L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, descendants2,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        Comment child3 = new Comment(4L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        children.add(child1);
        children.add(child2);
        children.add(child3);

        Comment comment = new Comment(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, children,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        // Exercise
        int totalDescendants = comment.getDescendantCount(3);

        Assert.assertEquals(8, totalDescendants);
    }

    @Test
    public void testGetDescendantCountMaxDepthLimit() {


        Set<Comment> descendants1 = new HashSet<>();
        Set<Comment> descendants2 = new HashSet<>();


        Comment descendant1 = new Comment(20L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        Comment descendant2 = new Comment(30L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        Comment descendant3 = new Comment(40L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        Comment descendant4 = new Comment(50L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        Comment descendant5 = new Comment(60L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        descendants1.add(descendant1);
        descendants1.add(descendant2);
        descendants1.add(descendant3);

        descendants2.add(descendant4);
        descendants2.add(descendant5);

        Set<Comment> children = new HashSet<>();

        Comment child1 = new Comment(2L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, descendants1,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        Comment child2 = new Comment(3L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, descendants2,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        Comment child3 = new Comment(4L, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        children.add(child1);
        children.add(child2);
        children.add(child3);

        Comment comment = new Comment(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, children,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, DEFAULT_LIKES);

        // Exercise
        int totalDescendants = comment.getDescendantCount(2);

        Assert.assertEquals(3, totalDescendants);
    }

    @Test
    public void testRemoveLike() {

        User realUser = Mockito.mock(User.class);
        Mockito.when(realUser.getId()).thenReturn(10L);

        User fakeUser = Mockito.mock(User.class);
        Mockito.lenient().when(fakeUser.getId()).thenReturn(11L);

        Set<CommentLike> likes = new HashSet<>();

        CommentLike realLike = Mockito.mock(CommentLike.class);
        Mockito.when(realLike.getUser()).thenReturn(realUser);

        CommentLike fakeLike = Mockito.mock(CommentLike.class);
        Mockito.lenient().when(fakeLike.getUser()).thenReturn(fakeUser);

        likes.add(realLike);
        likes.add(fakeLike);

        Comment comment = new Comment(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, likes);

        // Exercise
        comment.removeLike(realUser);

        Mockito.verify(realUser).removeCommentLike(realLike);

        Assert.assertEquals(1, likes.size());

        Assert.assertTrue(likes.contains(fakeLike));

        Assert.assertFalse(likes.contains(realLike));
    }

    @Test
    public void testLikeCreation() {

        Comment comment = new Comment(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, new HashSet<>());

        User user = Mockito.mock(User.class);

        // Exercise
        comment.like(user, 1);

        Mockito.verify(user).addCommentLike(Mockito.any(CommentLike.class));

        Assert.assertEquals(1, comment.getLikes().size());
    }

    @Test
    public void testLikeModification() {

        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(1L);

        Set<CommentLike> likes = new HashSet<>();

        CommentLike like = Mockito.mock(CommentLike.class);
        Mockito.when(like.getUser()).thenReturn(user);

        likes.add(like);

        Comment comment = new Comment(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_POST, DEFAULT_PARENT, DEFAULT_CHILDREN,
                DEFAULT_BODY, DEFAULT_EDITED, DEFAULT_LAST_EDITED, DEFAULT_USER, DEFAULT_ENABLED, likes);

        // Exercise
        comment.like(user, -1);

        Mockito.verify(like).setValue(-1);

        Assert.assertEquals(1, likes.size());
    }
}
