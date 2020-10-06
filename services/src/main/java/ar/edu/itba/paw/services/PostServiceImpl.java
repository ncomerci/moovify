package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PostCategoryDao categoryDao;

    @Override
    public long register(String title, String body, long category, long user, Set<String> tags, Set<Long> movies){
        return postDao.register(title, body.trim(), body.split("\\s+").length, category, user, tags, movies, true);
    }

    @Override
    public void delete(long id) {
        postDao.delete(id);
    }

    @Override
    public void likePost(long post_id, long user_id, boolean flag, int value) {
        if( userDao.hasUserLiked(user_id, post_id) != value)
            postDao.removeLike(post_id, user_id);
        if( value != 0)
            postDao.likePost(post_id,user_id,value);
    }

    @Override
    public Optional<Post> findPostById(long id) {
        return postDao.findPostById(id);
    }


    @Override
    public PaginatedCollection<Post> findPostsByMovieId(long movie_id, int pageNumber, int pageSize) {
        return postDao.findPostsByMovieId(movie_id, PostDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Post> findPostsByUserId(long user_id, int pageNumber, int pageSize) {
        return postDao.findPostsByUserId(user_id, PostDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Post> getAllPostsOrderByNewest(int pageNumber, int pageSize) {
        return postDao.getAllPosts(PostDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Post> getAllPostsOrderByOldest(int pageNumber, int pageSize) {
        return postDao.getAllPosts(PostDao.SortCriteria.OLDEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Post> getAllPostsOrderByHottest(int pageNumber, int pageSize) {
        return postDao.getAllPosts(PostDao.SortCriteria.HOTTEST, pageNumber, pageSize);
    }

    @Override
    public Collection<PostCategory> getAllPostCategories() {
        return categoryDao.getAllPostCategories();
    }
}