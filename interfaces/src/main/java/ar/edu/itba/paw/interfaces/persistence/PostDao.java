package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Post;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface PostDao {

    Optional<Post> findById(long id);

    Collection<Post> findPostsByTitle(String title);

    Collection<Post> findPostsByMovieId(long id);

    Collection<Post> findPostsByMovieTitle(String movie_title);

    Post register(String title, String email, String body, Set<Long> movies);

    Collection<Post> getAllPosts();
}
