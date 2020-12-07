package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.MovieDao;
import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.exceptions.*;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    private PostDao postDao;

    @Autowired
    private PostCategoryDao categoryDao;

    @Autowired
    private MovieDao movieDao;

    private final static Map<String, PostDao.SortCriteria> sortCriteriaMap = initializeSortCriteriaMap();

    private static Map<String, PostDao.SortCriteria> initializeSortCriteriaMap() {
        final Map<String, PostDao.SortCriteria> sortCriteriaMap = new LinkedHashMap<>();

        sortCriteriaMap.put("newest", PostDao.SortCriteria.NEWEST);
        sortCriteriaMap.put("oldest", PostDao.SortCriteria.OLDEST);
        sortCriteriaMap.put("hottest", PostDao.SortCriteria.HOTTEST);

        return sortCriteriaMap;
    }

    @Transactional
    @Override
    public Post register(String title, String body, PostCategory category, User user, Set<String> tags, Set<Long> moviesId) {

        final Collection<Movie> movies = movieDao.findMoviesById(moviesId);

        final Post post = postDao.register(title, body.trim(),
                body.split("\\s+").length, category, user, tags, new HashSet<>(movies), true);

        LOGGER.info("Created Post {}", post.getId());

        return post;
    }

    @Transactional
    @Override
    public void deletePost(Post post) throws DeletedDisabledModelException {

        if(!post.isEnabled())
            throw new DeletedDisabledModelException();

        LOGGER.info("Delete Post {}", post.getId());

        post.delete();
    }

    @Transactional
    @Override
    public void restorePost(Post post) throws RestoredEnabledModelException {

        if(post.isEnabled())
            throw new RestoredEnabledModelException();

        LOGGER.info("Restore Post {}", post.getId());

        post.restore();
    }

    @Transactional
    @Override
    public void likePost(Post post, User user, int value) throws IllegalPostLikeException {

        if(!post.isEnabled())
            throw new IllegalPostLikeException();

        if(post.getLikeValue(user) == value)
            return;

        if(value == 0) {
            LOGGER.info("Delete Like: User {} Post {}", user.getId(), post.getId());
            post.removeLike(user);
        }

        else if(value == -1 || value == 1) {
            LOGGER.info("Like: User {} Post {} Value {}", user.getId(), post.getId(), value);
            post.like(user, value);
        }
    }

    @Transactional
    @Override
    public void editPost(User editor, Post post, String newBody) throws MissingPostEditPermissionException, IllegalPostEditionException {
        Objects.requireNonNull(newBody);

        guaranteePostEditionPermissions(editor, post);

        post.setBody(newBody.trim());
    }

    @Override
    public void guaranteePostEditionPermissions(User editor, Post post) throws IllegalPostEditionException, MissingPostEditPermissionException {

        if(!post.isEnabled())
            throw new IllegalPostEditionException();

        if(!editor.equals(post.getUser()))
            throw new MissingPostEditPermissionException();
    }

    @Override
    public int getLikeValue(Post post, User user) {
        return postDao.getLikeValue(post, user);
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
    public PaginatedCollection<Post> getAllPosts(String sortCriteria, int pageNumber, int pageSize) {
        return postDao.getAllPosts(getPostSortCriteria(sortCriteria), pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> findPostsByMovie(Movie movie, String sortCriteria, int pageNumber, int pageSize) {
        return postDao.findPostsByMovie(movie, getPostSortCriteria(sortCriteria), pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> findPostsByUser(User user, String sortCriteria, int pageNumber, int pageSize) {
        return postDao.findPostsByUser(user, getPostSortCriteria(sortCriteria), pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> getFollowedUsersPosts(User user, String sortCriteria, int pageNumber, int pageSize) {
        return postDao.getFollowedUsersPosts(user, getPostSortCriteria(sortCriteria), pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Post> getUserBookmarkedPosts(User user, String sortCriteria, int pageNumber, int pageSize) {
        return postDao.getUserFavouritePosts(user, getPostSortCriteria(sortCriteria), pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<PostLike> getPostLikes(Post post, String sortCriteria, int pageNumber, int pageSize) {
        return postDao.getPostLikes(post, sortCriteria, pageNumber, pageSize);
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

    @Override
    public PostDao.SortCriteria getPostSortCriteria(String sortCriteriaName) {
        if(sortCriteriaName != null && sortCriteriaMap.containsKey(sortCriteriaName))
            return sortCriteriaMap.get(sortCriteriaName);

        else
            throw new InvalidSortCriteriaException();
    }

    @Override
    public Collection<String> getPostSortOptions() {
        return sortCriteriaMap.keySet();
    }

}