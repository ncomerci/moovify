package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.exceptions.*;
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

    @Test(expected = DeletedDisabledModelException.class)
    public void testDeleteDisabledPost() throws DeletedDisabledModelException {

        Post post = Mockito.mock(Post.class);
        Mockito.when(post.isEnabled()).thenReturn(false);

        postService.deletePost(post);
    }

    @Test(expected = RestoredEnabledModelException.class)
    public void testRestoreEnabledPost() throws RestoredEnabledModelException {

        Post post = Mockito.mock(Post.class);
        Mockito.when(post.isEnabled()).thenReturn(true);

        postService.restorePost(post);
    }

    @Test
    public void testLikePostRemove() throws IllegalPostLikeException {

        Post post = Mockito.mock(Post.class);
        User user = Mockito.mock(User.class);

        Mockito.when(post.isEnabled()).thenReturn(true);
        Mockito.when(post.getVoteValue(user)).thenReturn(UP_VOTE_VALUE);

        postService.likePost(post, user,0);

        Mockito.verify(post).removeVote(Mockito.any());
    }
    
    @Test
    public void testLikePostGiveUpVote() throws IllegalPostLikeException {

        Post post = Mockito.mock(Post.class);
        User user = Mockito.mock(User.class);

        Mockito.when(post.isEnabled()).thenReturn(true);
        Mockito.when(post.getVoteValue(user)).thenReturn(0);

        postService.likePost(post, user, UP_VOTE_VALUE);

        Mockito.verify(post).vote(Mockito.eq(user), Mockito.eq(UP_VOTE_VALUE));
    }

    @Test
    public void testLikePostGiveDownVote() throws IllegalPostLikeException {

        Post post = Mockito.mock(Post.class);
        User user = Mockito.mock(User.class);

        Mockito.when(post.isEnabled()).thenReturn(true);
        Mockito.when(post.getVoteValue(user)).thenReturn(0);

        postService.likePost(post, user, DOWN_VOTE_VALUE);

        Mockito.verify(post).vote(Mockito.eq(user), Mockito.eq(DOWN_VOTE_VALUE));
    }

    @Test(expected = IllegalPostLikeException.class)
    public void testLikePostDisabledPost() throws IllegalPostLikeException {

        Post post = Mockito.mock(Post.class);
        User user = Mockito.mock(User.class);

        Mockito.when(post.isEnabled()).thenReturn(false);

        postService.likePost(post, user, DOWN_VOTE_VALUE);
    }

    @Test(expected = IllegalPostEditionException.class)
    public void testEditDisabledPost() throws MissingPostEditPermissionException, IllegalPostEditionException {

        Post post = Mockito.mock(Post.class);
        Mockito.when(post.isEnabled()).thenReturn(false);

        User user = Mockito.mock(User.class);

        postService.editPost(user, post, "");
    }

    @Test(expected = MissingPostEditPermissionException.class)
    public void testEditPostEditorLackingPermissions() throws MissingPostEditPermissionException, IllegalPostEditionException {

        Post post = Mockito.mock(Post.class);
        Mockito.when(post.isEnabled()).thenReturn(true);
        Mockito.when(post.getUser()).thenReturn(Mockito.mock(User.class));

        User user = Mockito.mock(User.class);

        postService.editPost(user, post, "");
    }

    @Test(expected = IllegalPostEditionException.class)
    public void testGuaranteePostEditionPermissionsPostDisabled() throws MissingPostEditPermissionException, IllegalPostEditionException {

        Post post = Mockito.mock(Post.class);
        Mockito.when(post.isEnabled()).thenReturn(false);

        User user = Mockito.mock(User.class);

        postService.guaranteePostEditionPermissions(user, post);
    }

    @Test(expected = MissingPostEditPermissionException.class)
    public void testGuaranteePostEditionPermissionsEditorLackingPermissions() throws MissingPostEditPermissionException, IllegalPostEditionException {

        Post post = Mockito.mock(Post.class);
        Mockito.when(post.isEnabled()).thenReturn(true);
        Mockito.when(post.getUser()).thenReturn(Mockito.mock(User.class));

        User user = Mockito.mock(User.class);

        postService.guaranteePostEditionPermissions(user, post);
    }
}