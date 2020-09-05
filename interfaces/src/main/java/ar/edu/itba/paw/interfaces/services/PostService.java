package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Post;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface PostService {

    Optional<Post> findById(long id);

    Collection<Post> findPostsByTitle(String title);

    Collection<Post> findPostsByMovieTitle(String movie_title);

    Collection<Post> findPostsByMovieId(long movie_id);

    Post register(String title, String email, String body, Set<Long> movies);

    Collection<Post> getAllPosts();

}