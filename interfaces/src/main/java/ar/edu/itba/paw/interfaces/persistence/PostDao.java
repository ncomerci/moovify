package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Post;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface PostDao {

    Post register(String title, String email, String body, Set<Long> movies);

    Optional<Post> findPostById(long id, boolean withMovies);

    Collection<Post> findPostsByTitle(String title, boolean withMovies);

    Collection<Post> findPostsByMovieId(long id, boolean withMovies);

    Collection<Post> findPostsByMovieTitle(String movie_title, boolean withMovies);

    Collection<Post> getAllPosts(boolean withMovies);

    Collection<Post> findPostsByPostAndMovieTitle(String title, boolean withMovies);
}
