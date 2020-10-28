package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public interface PostDao {

    enum SortCriteria {
        NEWEST, OLDEST, HOTTEST
    }

    Post register(String title, String body, int wordCount, PostCategory category, User user, Set<String> tags, Set<Long> movies, boolean enabled);

    Optional<Post> findPostById(long id);

    Optional<Post> findDeletedPostById(long id);

    PaginatedCollection<Post> getAllPosts(SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> findPostsByMovie(Movie movie, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> findPostsByUser(User user, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> getDeletedPosts(SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchPosts(String query, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchDeletedPosts(String query, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchPostsByCategory(String query, String category, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchPostsOlderThan(String query, LocalDateTime fromDate, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchPostsByCategoryAndOlderThan(String query, String category, LocalDateTime fromDate, SortCriteria sortCriteria, int pageNumber, int pageSize);
}