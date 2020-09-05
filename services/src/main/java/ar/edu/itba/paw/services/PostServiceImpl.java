package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.interfaces.services.PostService;

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
    public Set<Post> findPosts(String searchParam) {
        return postDao.findPosts(searchParam);
    }

    @Override
    public Set<Post> findPostsByTitle(String title) {
        return postDao.findPostsByTitle(title);
    }

    @Override
    public Set<Post> findPostsByMovieTitle(String movie_title) {
        return postDao.findPostsByMovieTitle(movie_title);
    }

    @Override
    public Set<Post> findPostsByMovieId(long movie_id) {
        return postDao.findPostsByMovieId(movie_id);
    }

    @Override
    public Post register(String title, String email, String body, Set<Long> movies){
        return postDao.register(title, email, body, movies);
    }

    @Override
    public Set<Post> getAllPosts() {
        return postDao.getAllPosts();
    }


}