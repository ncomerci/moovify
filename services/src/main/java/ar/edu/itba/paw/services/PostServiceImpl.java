package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.PostService;
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
    private PostCategoryDao categoryDao;

    @Override
    public long register(String title, String body, long category, long user, Set<String> tags, Set<Long> movies){
        return postDao.register(title, body.trim(), body.split("\\s+").length, category, user, tags, movies, true);
    }

    @Override
    public void likePost(long post_id, long user_id, boolean value) {
        if(value)
            postDao.likePost(post_id, user_id);
        else
            postDao.removeLike(post_id, user_id);
    }

    @Override
    public Optional<Post> findPostById(long id) {
        return postDao.findPostById(id);
    }


    @Override
    public Collection<Post> findPostsByMovieId(long movie_id) {
        return postDao.findPostsByMovieId(movie_id, PostDao.SortCriteria.NEWEST);
    }

    @Override
    public Collection<Post> findPostsByUserId(long user_id) {
        return postDao.findPostsByUserId(user_id, PostDao.SortCriteria.NEWEST);
    }

    @Override
    public Collection<Post> getAllPostsOrderByNewest() {
        return postDao.getAllPosts(PostDao.SortCriteria.NEWEST);
    }

    @Override
    public Collection<Post> getAllPostsOrderByOldest() {
        return postDao.getAllPosts(PostDao.SortCriteria.OLDEST);
    }

    @Override
    public Collection<Post> getAllPostsOrderByHottest() {
        return postDao.getAllPosts(PostDao.SortCriteria.HOTTEST);
    }

    @Override
    public Collection<PostCategory> getAllPostCategories() {
        return categoryDao.getAllPostCategories();
    }
}