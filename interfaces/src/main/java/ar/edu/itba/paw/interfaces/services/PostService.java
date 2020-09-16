package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Post;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public interface PostService {

    Post register(String title, String email, String body, Collection<String> tags, Set<Long> movies);

    Optional<Post> findPostById(long id);

    Collection<Post> findPostsByMovieId(long movie_id);

    Collection<Post> getAllPostsOrderByNewest();

    Collection<Post> getAllPostsOrderByOldest();
}