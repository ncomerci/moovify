package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceImplTest {

    @Mock
    private PostDao postDao;

    @Mock
    private PostCategoryDao categoryDao;

    @InjectMocks
    private final PostServiceImpl postService = new PostServiceImpl();

    @Test
    public void testLikePostRemove() {

        postService.likePost(Mockito.mock(Post.class), Mockito.mock(User.class),0);
    }
    
    @Test
    public void testLikePostGiveUpVote() {

        postService.likePost(Mockito.mock(Post.class), Mockito.mock(User.class),1);
    }

    @Test
    public void testLikePostGiveDownVote() {

        postService.likePost(Mockito.mock(Post.class), Mockito.mock(User.class),-1);
    }

}