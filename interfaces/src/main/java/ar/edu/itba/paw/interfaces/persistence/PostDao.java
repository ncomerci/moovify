package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public interface PostDao {

    enum SortCriteria {
        NEWEST, OLDEST, HOTTEST
    }

    long register(String title, String body, int wordCount, long category, long user, Set<String> tags, Set<Long> movies);

    Optional<Post> findPostById(long id);

    int getPostsTotalCount();

    PaginatedCollection<Post> getAllPosts(SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> findPostsByMovieId(long movie_id, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> findPostsByUserId(long user_id, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchPosts(String query, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchPostsByCategory(String query, String category, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchPostsOlderThan(String query, LocalDateTime fromDate, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchPostsByCategoryAndOlderThan(String query, String category, LocalDateTime fromDate, SortCriteria sortCriteria, int pageNumber, int pageSize);
}