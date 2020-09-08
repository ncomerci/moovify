package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.CommentService;
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
    public Post register(String title, String email, String body, Collection<String> tags, Set<Long> movies){
        return postDao.register(title, email, body, tags, movies);
    }

    @Override
    public Optional<Post> findPostById(long id, boolean withMovies, boolean withComments) {
        return postDao.findPostById(id, withMovies, withComments);
    }

    @Override
    public Collection<Post> findPostsByPostAndMovieTitle(String searchParam, boolean withMovies, boolean withComments) {
        return postDao.findPostsByPostAndMovieTitle(searchParam, withMovies, withComments);
    }

    @Override
    public Collection<Post> findPostsByTitle(String title, boolean withMovies, boolean withComments) {
        return postDao.findPostsByTitle(title, withMovies, withComments);
    }

    @Override
    public Collection<Post> findPostsByMovieTitle(String movie_title, boolean withMovies, boolean withComments) {
        return postDao.findPostsByMovieTitle(movie_title, withMovies, withComments);
    }

    @Override
    public Collection<Post> findPostsByMovieId(long movie_id, boolean withMovies, boolean withComments) {
        return postDao.findPostsByMovieId(movie_id, withMovies, withComments);
    }

    @Override
    public Collection<Post> getAllPosts(boolean withMovies, boolean withComments) {
        return postDao.getAllPosts(withMovies, withComments);
    }
}