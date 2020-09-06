package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private PostDao postDao;

    @Override
    public Collection<Post> findPostsByPostAndMovieTitle(String title, boolean withMovies) {
        return postDao.findPostsByPostAndMovieTitle(title, withMovies);
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
}
