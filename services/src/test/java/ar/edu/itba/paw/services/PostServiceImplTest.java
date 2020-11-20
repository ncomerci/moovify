package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.exceptions.IllegalPostLikeException;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceImplTest {

    private static final int UP_VOTE_VALUE = 1;
    private static final int DOWN_VOTE_VALUE = -1;

    @InjectMocks
    private final PostServiceImpl postService = new PostServiceImpl();

    @Test
    public void testLikePostRemove() throws IllegalPostLikeException {

        Post post = Mockito.mock(Post.class);
        User user = Mockito.mock(User.class);

        Mockito.when(post.isEnabled()).thenReturn(true);
        Mockito.when(post.getLikeValue(user)).thenReturn(UP_VOTE_VALUE);

        postService.likePost(post, user,0);

        Mockito.verify(post).removeLike(Mockito.any());
    }
    
    @Test
    public void testLikePostGiveUpVote() throws IllegalPostLikeException {

        Post post = Mockito.mock(Post.class);
        User user = Mockito.mock(User.class);

        Mockito.when(post.isEnabled()).thenReturn(true);
        Mockito.when(post.getLikeValue(user)).thenReturn(0);

        postService.likePost(post, user, UP_VOTE_VALUE);

        Mockito.verify(post).like(Mockito.eq(user), Mockito.eq(UP_VOTE_VALUE));
    }

    @Test
    public void testLikePostGiveDownVote() throws IllegalPostLikeException {

        Post post = Mockito.mock(Post.class);
        User user = Mockito.mock(User.class);

        Mockito.when(post.isEnabled()).thenReturn(true);
        Mockito.when(post.getLikeValue(user)).thenReturn(0);

        postService.likePost(post, user, DOWN_VOTE_VALUE);

        Mockito.verify(post).like(Mockito.eq(user), Mockito.eq(DOWN_VOTE_VALUE));
    }

    @Test(expected = IllegalPostLikeException.class)
    public void testLikePostDisabledPost() throws IllegalPostLikeException {

        Post post = Mockito.mock(Post.class);
        User user = Mockito.mock(User.class);

        Mockito.when(post.isEnabled()).thenReturn(false);

        postService.likePost(post, user, DOWN_VOTE_VALUE);
    }
}