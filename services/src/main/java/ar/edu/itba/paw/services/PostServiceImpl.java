package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    private PostDao postDao;

    @Autowired
    private PostCategoryDao categoryDao;

    @Autowired
    private MovieDao movieDao;

    @Transactional
    @Override
    public Post register(String title, String body, PostCategory category, User user, Set<String> tags, Set<Long> moviesId) {

        Objects.requireNonNull(body);

        Objects.requireNonNull(title, "PostDao: register: title can't be null");
        Objects.requireNonNull(body);
        Objects.requireNonNull(moviesId);
        Objects.requireNonNull(category);
        Objects.requireNonNull(user);

        final Collection<Movie> movies = movieDao.findMoviesById(moviesId);

        final Post post = postDao.register(title, body.trim(),
                body.split("\\s+").length, category, user, tags, movies, true);

        LOGGER.info("Created Post {}", post.getId());

        return post;
    }

    @Transactional
    @Override
    public void deletePost(Post post) {
        post.delete();
    }

    @Transactional
    @Override
    public void restorePost(Post post) {
        post.restore();
    }

    @Transactional
    @Override
    public void likePost(Post post, User user, int value) {

        if(value == 0)
            post.removeLike(user);

        else if(value == -1 || value == 1)
            post.like(user, value);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Post> findPostById(long id) {
        return postDao.findPostById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Post> findDeletedPostById(long id) {
        return postDao.findDeletedPostById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> findPostsByMovie(Movie movie, int pageNumber, int pageSize) {
        return postDao.findPostsByMovie(movie, PostDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> findPostsByUser(User user, int pageNumber, int pageSize) {
        return postDao.findPostsByUser(user, PostDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> getAllPostsOrderByNewest(int pageNumber, int pageSize) {
        return postDao.getAllPosts(PostDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> getAllPostsOrderByOldest(int pageNumber, int pageSize) {
        return postDao.getAllPosts(PostDao.SortCriteria.OLDEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> getAllPostsOrderByHottest(int pageNumber, int pageSize) {
        return postDao.getAllPosts(PostDao.SortCriteria.HOTTEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<PostCategory> getAllPostCategories() {
        return categoryDao.getAllPostCategories();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PostCategory> findCategoryById(long categoryId) {
        return categoryDao.findPostCategoryById(categoryId);
    }
}