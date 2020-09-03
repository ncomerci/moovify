package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Post;

import java.util.Optional;
import java.util.Set;

public interface PostService {

    Optional<Post> findById(long id);

    Set<Post> findPostsByTitle(String title);

    Set<Post> findPostsByMovieTitle(String movie_title);

    Set<Post> findPostsByMovieId(long movie_id);

    Post register(String title, String email, String body, Set<Long> movies);

    Set<Post> getAllPosts();

}