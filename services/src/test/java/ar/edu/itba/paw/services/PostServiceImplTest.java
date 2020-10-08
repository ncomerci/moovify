package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategory;
import ar.edu.itba.paw.models.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceImplTest {

    private static final String TITLE = "Title";
    private static final String BODY = "Body";
    private static final Set<String> TAGS = new HashSet<>(Collections.singletonList("tag"));
    private static final Set<Long> MOVIE = new HashSet<>(Collections.singletonList(123L));
    private static final long ID = 123;
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 5;
    private static final long CATEGORY_ID = 123;

    @Mock
    private PostDao postDao;

    @Mock
    private PostCategoryDao categoryDao;

    @InjectMocks
    private final PostServiceImpl postService = new PostServiceImpl();

    @Test
    public void testRegister() {

        postService.register(TITLE, BODY, Mockito.mock(PostCategory.class), Mockito.mock(User.class), TAGS, MOVIE);
    }

    @Test
    public void testDeletePost() {

        postService.deletePost(Mockito.mock(Post.class));
    }

    @Test
    public void testRestorePost() {

        postService.restorePost(Mockito.mock(Post.class));
    }

    @Test
    public void testLikePostRemove() {

        postService.likePost(Mockito.mock(Post.class), Mockito.mock(User.class),0);
    }
    
    @Test
    public void testLikePostGive() {

        postService.likePost(Mockito.mock(Post.class), Mockito.mock(User.class),1);
    }

    @Test
    public void testFindPostById() {

        postService.findPostById(ID);
    }

    @Test
    public void testFindPostsByMovie() {

        postService.findPostsByMovie(Mockito.mock(Movie.class), PAGE_NUMBER, PAGE_SIZE);
    }

    @Test
    public void testFindPostsByUser() {

        postService.findPostsByUser(Mockito.mock(User.class), PAGE_NUMBER, PAGE_SIZE);
    }

    @Test
    public void getAllPostsOrderByNewest() {

        postService.getAllPostsOrderByNewest(PAGE_NUMBER, PAGE_SIZE);
    }

    @Test
    public void getAllPostsOrderByOldest() {

        postService.getAllPostsOrderByOldest(PAGE_NUMBER, PAGE_SIZE);
    }

    @Test
    public void getAllPostsOrderByHottest() {

        postService.getAllPostsOrderByHottest(PAGE_NUMBER, PAGE_SIZE);
    }

    @Test
    public void getAllPostCategories() {

        postService.getAllPostCategories();
    }

    @Test
    public void findCategoryById() {

        postService.findCategoryById(CATEGORY_ID);
    }
}