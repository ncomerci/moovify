package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.EnumSet;
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
        return postDao.register(title, body, category, user, tags, movies);
    }

    @Override
    public Optional<Post> findPostById(long id) {
        return postDao.findPostById(id, EnumSet.allOf(PostDao.FetchRelation.class));
    }

    @Override
    public Collection<Post> findPostsByMovieId(long movie_id) {
        return postDao.findPostsByMovieId(movie_id, EnumSet.noneOf(PostDao.FetchRelation.class));
    }

    @Override
    public Collection<Post> getAllPostsOrderByNewest() {
        return postDao.getAllPosts(EnumSet.noneOf(PostDao.FetchRelation.class), PostDao.SortCriteria.NEWEST);
    }

    @Override
    public Collection<Post> getAllPostsOrderByOldest() {
        return postDao.getAllPosts(EnumSet.noneOf(PostDao.FetchRelation.class), PostDao.SortCriteria.OLDEST);
    }

    @Override
    public Collection<PostCategory> getAllPostCategories() {
        return categoryDao.getAllPostCategories();
    }
}