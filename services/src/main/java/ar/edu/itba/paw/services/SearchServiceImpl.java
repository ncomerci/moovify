package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private PostDao postDao;

    @Override
    public Set<Post> searchPostsbyTitle(String title) {
        return postDao.findPostsByTitle(title);
    }

    @Override
    public Set<Post> searchPostsbyMovieTitle(String movie_title) {
        return postDao.findPostsByMovieTitle(movie_title);
    }

    @Override
    public Set<Post> searchPostsbyMovieId(long movie_id) {
        return postDao.findPostsByMovieId(movie_id);
    }


}
