package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.interfaces.services.PostService;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostDao postDao;

    @Override
    public Post register(String title, String email, String body, Set<Long> movies){
        return postDao.register(title, email, body, movies);
    }

    @Override
    public Optional<Post> findPostById(long id, boolean withMovies) {
        return postDao.findPostById(id, withMovies);
    }

    @Override
    public Collection<Post> findPostsByPostAndMovieTitle(String searchParam, boolean withMovies) {
        return postDao.findPostsByPostAndMovieTitle(searchParam, withMovies);
    }

    @Override
    public Collection<Post> findPostsByTitle(String title, boolean withMovies) {
        return postDao.findPostsByTitle(title, withMovies);
    }

    @Override
    public Collection<Post> findPostsByMovieTitle(String movie_title, boolean withMovies) {
        return postDao.findPostsByMovieTitle(movie_title, withMovies);
    }

    @Override
    public Collection<Post> findPostsByMovieId(long movie_id, boolean withMovies) {
        return postDao.findPostsByMovieId(movie_id, withMovies);
    }

    @Override
    public Collection<Post> getAllPosts(boolean withMovies) {
        return postDao.getAllPosts(withMovies);
    }
}