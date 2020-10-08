package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.*;
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
    public Post register(String title, String body, PostCategory category, User user, Set<String> tags, Set<Long> movies){
        return postDao.register(title, body.trim(),
                body.split("\\s+").length, category, user, tags, movies, true);
    }

    @Override
    public void deletePost(Post post) {
        postDao.deletePost(post);
    }

    @Override
    public void restorePost(Post post) {
        postDao.restorePost(post);
    }

    @Override
    public void likePost(Post post, User user, int value) {

        if(value == 0)
            postDao.removeLike(post, user);

        else if(value == -1 || value == 1)
            postDao.likePost(post, user, value);
    }

    @Override
    public Optional<Post> findPostById(long id) {
        return postDao.findPostById(id);
    }


    @Override
    public PaginatedCollection<Post> findPostsByMovie(Movie movie, int pageNumber, int pageSize) {
        return postDao.findPostsByMovie(movie, PostDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Post> findPostsByUser(User user, int pageNumber, int pageSize) {
        return postDao.findPostsByUser(user, PostDao.SortCriteria.NEWEST, pageNumber, pageSize);
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

    @Override
    public Optional<PostCategory> findCategoryById(long categoryId) {
        return categoryDao.findPostCategoryById(categoryId);
    }
}