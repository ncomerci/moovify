package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Post;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface PostService {

    Post register(String title, String email, String body, Collection<String> tags, Set<Long> movies);

    Optional<Post> findPostById(long id, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByMovieId(long movie_id, boolean withMovies, boolean withComments);

    Collection<Post> getAllPostsOrderByNewest(boolean withMovies, boolean withComments);

    Collection<Post> getAllPostsOrderByOldest(boolean withMovies, boolean withComments);
}