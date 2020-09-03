package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Post;

import java.util.Optional;
import java.util.Set;

public interface PostDao {

    Optional<Post> findById(long id);

    Set<Post> findPostsByTitle(String title);

    Set<Post> findPostsByMovieId(long id);

    Set<Post> findPostsByMovieTitle(String movie_title);

    Post register(String title, String email, String body, Set<Long> movies);

    Set<Post> getAllPosts();
}
