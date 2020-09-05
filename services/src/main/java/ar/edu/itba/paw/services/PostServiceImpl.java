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
    public Optional<Post> findById(long id) {
        return postDao.findById(id);
    }

    @Override
    public Collection<Post> findPosts(String searchParam) {
        return postDao.findPosts(searchParam);
    }

    @Override
    public Collection<Post> findPostsByTitle(String title) {
        return postDao.findPostsByTitle(title);
    }

    @Override
    public Collection<Post> findPostsByMovieTitle(String movie_title) {
        return postDao.findPostsByMovieTitle(movie_title);
    }

    @Override
    public Collection<Post> findPostsByMovieId(long movie_id) {
        return postDao.findPostsByMovieId(movie_id);
    }

    @Override
    public Post register(String title, String email, String body, Set<Long> movies){
        return postDao.register(title, email, body, movies);
    }

    @Override
    public Collection<Post> getAllPosts() {
        return postDao.getAllPosts();
    }


}