package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Post;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface PostDao {

    Post register(String title, String email, String body, Collection<String> tags, Set<Long> movies);

    Optional<Post> findPostById(long id, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByTitleOrderByNewest(String title, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByTitleOrderByOldest(String title, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByMoviesOrderByNewest(String title, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByMoviesOrderByOldest(String title, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByTagsOrderByNewest(String title, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByTagsOrderByOldest(String title, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByTitleAndMoviesOrderByNewest(String title, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByTitleAndMoviesOrderByOldest(String title, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByTitleAndTagsOrderByNewest(String title, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByTitleAndTagsOrderByOldest(String title, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByTagsAndMoviesOrderByNewest(String title, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByTagsAndMoviesOrderByOldest(String title, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByTitleAndTagsAndMoviesOrderByNewest(String title, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByTitleAndTagsAndMoviesOrderByOldest(String title, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByMovieId(long id, boolean withMovies, boolean withComments);

    Collection<Post> findPostsByMovieTitle(String movie_title, boolean withMovies, boolean withComments);

    Collection<Post> getAllPosts(boolean withMovies, boolean withComments);

    Collection<Post> findPostsByPostAndMovieTitle(String title, boolean withMovies, boolean withComments);
}
