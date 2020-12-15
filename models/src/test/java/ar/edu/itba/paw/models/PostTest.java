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
public class PostTest {

    private static final long DEFAULT_ID = 1L;
    private static final LocalDateTime DEFAULT_CREATION_DATE = LocalDateTime.now();
    private static final String DEFAULT_TITLE = "";
    private static final String DEFAULT_BODY = "";
    private static final int DEFAULT_WORD_COUNT = 1;
    private static final PostCategory DEFAULT_POST_CATEGORY = new PostCategory();
    private static final User DEFAULT_USER = new User();
    private static final Set<String> DEFAULT_TAGS = Collections.emptySet();
    private static final boolean DEFAULT_EDITED = false;
    private static final LocalDateTime DEFAULT_LAST_EDIT_DATE = null;
    private static final boolean DEFAULT_ENABLED = true;
    private static final Set<PostVote> DEFAULT_LIKES = Collections.emptySet();
    private static final Set<Movie> DEFAULT_MOVIES = Collections.emptySet();
    private static final Set<User> DEFAULT_BOOKMARKED = Collections.emptySet();
    private static final Set<Comment> DEFAULT_COMMENTS = Collections.emptySet();

    @Test
    public void testCalculateTotalLikes() {

        Set<PostVote> likes = new HashSet<>();
        PostVote like;

        // Add 20 likes: 10 positive 10 negative
        for (int i = 0; i < 10; i++) {

            like = Mockito.mock(PostVote.class);
            Mockito.when(like.getValue()).thenReturn(1);

            likes.add(like);

            like = Mockito.mock(PostVote.class);
            Mockito.when(like.getValue()).thenReturn(-1);

            likes.add(like);
        }

        // Add 2 positive likes
        like = Mockito.mock(PostVote.class);
        Mockito.when(like.getValue()).thenReturn(1);

        likes.add(like);

        like = Mockito.mock(PostVote.class);
        Mockito.when(like.getValue()).thenReturn(1);

        likes.add(like);

        Post post = new Post(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_TITLE, DEFAULT_BODY, DEFAULT_WORD_COUNT, DEFAULT_POST_CATEGORY,
                DEFAULT_USER, DEFAULT_TAGS, DEFAULT_EDITED, DEFAULT_LAST_EDIT_DATE, DEFAULT_ENABLED, likes, DEFAULT_BOOKMARKED,
                DEFAULT_MOVIES, DEFAULT_COMMENTS);

        // Exercise
        post.calculateTotalLikes();

        Assert.assertEquals(2L, post.getTotalVotes());
    }

    @Test
    public void testGetVoteValue() {

        long userRealId = 5L;
        int likeValue = -1;

        Set<PostVote> likes = new HashSet<>();
        PostVote like;
        User realUser;

        // Create real user with valid value
        realUser = Mockito.mock(User.class);
        Mockito.when(realUser.getId()).thenReturn(userRealId);

        User fakeUser1 = Mockito.mock(User.class);
        Mockito.lenient().when(fakeUser1.getId()).thenReturn(userRealId + 5L);

        User fakeUser2 = Mockito.mock(User.class);
        Mockito.lenient().when(fakeUser2.getId()).thenReturn(userRealId + 10L);

        like = Mockito.mock(PostVote.class);
        Mockito.when(like.getValue()).thenReturn(likeValue);
        Mockito.when(like.getUser()).thenReturn(realUser);

        likes.add(like);

        // Create other likes
        like = Mockito.mock(PostVote.class);
        Mockito.lenient().when(like.getUser()).thenReturn(fakeUser1);
        likes.add(like);

        like = Mockito.mock(PostVote.class);
        Mockito.lenient().when(like.getUser()).thenReturn(fakeUser2);
        likes.add(like);

        Post post = new Post(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_TITLE, DEFAULT_BODY, DEFAULT_WORD_COUNT, DEFAULT_POST_CATEGORY,
                DEFAULT_USER, DEFAULT_TAGS, DEFAULT_EDITED, DEFAULT_LAST_EDIT_DATE, DEFAULT_ENABLED, likes, DEFAULT_BOOKMARKED,
                DEFAULT_MOVIES, DEFAULT_COMMENTS);

        // Exercise
        int value = post.getVoteValue(realUser);

        Assert.assertEquals(likeValue, value);
    }

    @Test
    public void testGetVoteValueByUsername() {

        String userRealUsername = "realUsername";
        int likeValue = -1;

        Set<PostVote> likes = new HashSet<>();
        PostVote like;
        User realUser;

        // Create real user with valid value
        realUser = Mockito.mock(User.class);
        Mockito.when(realUser.getUsername()).thenReturn(userRealUsername);

        User fakeUser1 = Mockito.mock(User.class);
        Mockito.lenient().when(fakeUser1.getUsername()).thenReturn("fake1");

        User fakeUser2 = Mockito.mock(User.class);
        Mockito.lenient().when(fakeUser2.getUsername()).thenReturn("fake2");

        like = Mockito.mock(PostVote.class);
        Mockito.when(like.getValue()).thenReturn(likeValue);
        Mockito.when(like.getUser()).thenReturn(realUser);

        likes.add(like);

        // Create other likes
        like = Mockito.mock(PostVote.class);
        Mockito.lenient().when(like.getUser()).thenReturn(fakeUser1);
        likes.add(like);

        like = Mockito.mock(PostVote.class);
        Mockito.lenient().when(like.getUser()).thenReturn(fakeUser2);
        likes.add(like);

        Post post = new Post(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_TITLE, DEFAULT_BODY, DEFAULT_WORD_COUNT, DEFAULT_POST_CATEGORY,
                DEFAULT_USER, DEFAULT_TAGS, DEFAULT_EDITED, DEFAULT_LAST_EDIT_DATE, DEFAULT_ENABLED, likes, DEFAULT_BOOKMARKED,
                DEFAULT_MOVIES, DEFAULT_COMMENTS);

        // Exercise
        int value = post.getVoteValue(userRealUsername);

        Assert.assertEquals(likeValue, value);
    }

    @Test
    public void testHasBookmarked() {
        long userRealId = 5L;

        Set<User> bookmarked = new HashSet<>();
        User realUser;

        // Create real user with valid value
        realUser = Mockito.mock(User.class);
        Mockito.lenient().when(realUser.getId()).thenReturn(userRealId);

        User fakeUser1 = Mockito.mock(User.class);
        Mockito.lenient().when(fakeUser1.getId()).thenReturn(userRealId + 5L);

        User fakeUser2 = Mockito.mock(User.class);
        Mockito.lenient().when(fakeUser2.getId()).thenReturn(userRealId + 10L);

        bookmarked.add(realUser);
        bookmarked.add(fakeUser1);
        bookmarked.add(fakeUser2);

        Post post = new Post(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_TITLE, DEFAULT_BODY, DEFAULT_WORD_COUNT, DEFAULT_POST_CATEGORY,
                DEFAULT_USER, DEFAULT_TAGS, DEFAULT_EDITED, DEFAULT_LAST_EDIT_DATE, DEFAULT_ENABLED, DEFAULT_LIKES, bookmarked,
                DEFAULT_MOVIES, DEFAULT_COMMENTS);

        // Exercise
        boolean value = post.hasBookmarked(realUser);

        Assert.assertTrue(value);
    }

    @Test
    public void testHasBookmarkedByUsername() {
        String userRealUsername = "realUsername";

        Set<User> bookmarked = new HashSet<>();
        User realUser;

        // Create real user with valid value
        realUser = Mockito.mock(User.class);
        Mockito.when(realUser.getUsername()).thenReturn(userRealUsername);

        User fakeUser1 = Mockito.mock(User.class);
        Mockito.lenient().when(fakeUser1.getUsername()).thenReturn("fake1");

        User fakeUser2 = Mockito.mock(User.class);
        Mockito.lenient().when(fakeUser2.getUsername()).thenReturn("fake2");

        bookmarked.add(realUser);
        bookmarked.add(fakeUser1);
        bookmarked.add(fakeUser2);

        Post post = new Post(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_TITLE, DEFAULT_BODY, DEFAULT_WORD_COUNT, DEFAULT_POST_CATEGORY,
                DEFAULT_USER, DEFAULT_TAGS, DEFAULT_EDITED, DEFAULT_LAST_EDIT_DATE, DEFAULT_ENABLED, DEFAULT_LIKES, bookmarked,
                DEFAULT_MOVIES, DEFAULT_COMMENTS);

        // Exercise
        boolean value = post.hasBookmarked(userRealUsername);

        Assert.assertTrue(value);
    }

    @Test
    public void testRemoveVote() {

        User realUser = Mockito.mock(User.class);
        Mockito.when(realUser.getId()).thenReturn(10L);

        User fakeUser = Mockito.mock(User.class);
        Mockito.lenient().when(fakeUser.getId()).thenReturn(11L);

        Set<PostVote> likes = new HashSet<>();

        PostVote realLike = Mockito.mock(PostVote.class);
        Mockito.when(realLike.getUser()).thenReturn(realUser);

        PostVote fakeLike = Mockito.mock(PostVote.class);
        Mockito.lenient().when(fakeLike.getUser()).thenReturn(fakeUser);

        likes.add(realLike);
        likes.add(fakeLike);

        Post post = new Post(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_TITLE, DEFAULT_BODY, DEFAULT_WORD_COUNT, DEFAULT_POST_CATEGORY,
                DEFAULT_USER, DEFAULT_TAGS, DEFAULT_EDITED, DEFAULT_LAST_EDIT_DATE, DEFAULT_ENABLED, likes, DEFAULT_BOOKMARKED,
                DEFAULT_MOVIES, DEFAULT_COMMENTS);

        // Exercise
        post.removeVote(realUser);

        Mockito.verify(realUser).removePostLike(realLike);

        Assert.assertEquals(1, likes.size());

        Assert.assertTrue(likes.contains(fakeLike));

        Assert.assertFalse(likes.contains(realLike));
    }

    @Test
    public void testVoteCreation() {

        Post post = new Post(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_TITLE, DEFAULT_BODY, DEFAULT_WORD_COUNT, DEFAULT_POST_CATEGORY,
                DEFAULT_USER, DEFAULT_TAGS, DEFAULT_EDITED, DEFAULT_LAST_EDIT_DATE, DEFAULT_ENABLED, new HashSet<>(), DEFAULT_BOOKMARKED,
                DEFAULT_MOVIES, DEFAULT_COMMENTS);

        User user = Mockito.mock(User.class);

        // Exercise
        post.vote(user, 1);

        Mockito.verify(user).addPostLike(Mockito.any(PostVote.class));

        Assert.assertEquals(1, post.getVotes().size());
    }

    @Test
    public void testVoteModification() {

        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(1L);

        Set<PostVote> likes = new HashSet<>();

        PostVote like = Mockito.mock(PostVote.class);
        Mockito.when(like.getUser()).thenReturn(user);

        likes.add(like);

        Post post = new Post(DEFAULT_ID, DEFAULT_CREATION_DATE, DEFAULT_TITLE, DEFAULT_BODY, DEFAULT_WORD_COUNT, DEFAULT_POST_CATEGORY,
                DEFAULT_USER, DEFAULT_TAGS, DEFAULT_EDITED, DEFAULT_LAST_EDIT_DATE, DEFAULT_ENABLED, likes, DEFAULT_BOOKMARKED,
                DEFAULT_MOVIES, DEFAULT_COMMENTS);

        // Exercise
        post.vote(user, -1);

        Mockito.verify(like).setValue(-1);

        Assert.assertEquals(1, likes.size());
    }
}
