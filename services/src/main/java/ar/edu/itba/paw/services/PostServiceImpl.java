package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategory;
import ar.edu.itba.paw.models.User;
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
    public void restore(long id) {
        postDao.restore(id);
    }

    @Override
    public void likePost(Post post, User user, int value) {
        if(value == 0)
            postDao.removeLike(post.getId(), user.getId());
        else if(value == -1 || value == 1)
            postDao.likePost(post.getId(), user.getId(), value);
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
    public PaginatedCollection<Post> getDeletedPosts(int pageNumber, int pageSize) {
        return postDao.getDeletedPosts(PostDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public Collection<PostCategory> getAllPostCategories() {
        return categoryDao.getAllPostCategories();
    }
}