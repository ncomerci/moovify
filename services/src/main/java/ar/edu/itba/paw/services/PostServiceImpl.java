package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.interfaces.services.PostService;

import java.util.Collection;
import java.util.EnumSet;
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
    public Optional<Post> findPostById(long id) {
        return postDao.findPostById(id, EnumSet.allOf(PostDao.FetchRelation.class));
    }

    @Override
    public Collection<Post> findPostsByMovieId(long movie_id) {
        return postDao.findPostsByMovieId(movie_id, EnumSet.noneOf(PostDao.FetchRelation.class));
    }

    @Override
    public Collection<Post> getAllPostsOrderByNewest() {
        return postDao.getAllPostsOrderByNewest(EnumSet.noneOf(PostDao.FetchRelation.class));
    }

    @Override
    public Collection<Post> getAllPostsOrderByOldest() {
        return postDao.getAllPostsOrderByOldest(EnumSet.noneOf(PostDao.FetchRelation.class));
    }
}