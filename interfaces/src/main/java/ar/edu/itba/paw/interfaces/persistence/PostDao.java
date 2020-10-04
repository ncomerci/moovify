package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Post;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface PostDao {

    enum SortCriteria {
        NEWEST, OLDEST, HOTTEST
    }

    long register(String title, String body, int wordCount, long category, long user, Set<String> tags, Set<Long> movies, boolean enabled);

    void likePost(long post_id, long user_id);

    void removeLike(long post_id, long user_id);

    Optional<Post> findPostById(long id);

    Collection<Post> getAllPosts(SortCriteria sortCriteria);

    Collection<Post> findPostsByMovieId(long movie_id, SortCriteria sortCriteria);

    Collection<Post> findPostsByUserId(long user_id, SortCriteria sortCriteria);

    Collection<Post> searchPosts(String query, SortCriteria sortCriteria);

    Collection<Post> searchPostsByCategory(String query, String category, SortCriteria sortCriteria);

    Collection<Post> searchPostsOlderThan(String query, LocalDateTime fromDate, SortCriteria sortCriteria);

    Collection<Post> searchPostsByCategoryAndOlderThan(String query, String category, LocalDateTime fromDate, SortCriteria sortCriteria);
}