package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public interface PostDao {

    enum SortCriteria {
        NEWEST, OLDEST, HOTTEST
    }

    Post register(String title, String body, int wordCount, PostCategory category, User user, Set<String> tags, Set<Movie> movies, boolean enabled);

    Optional<Post> findPostById(long id);

    PaginatedCollection<Post> findPostsByMovie(Movie movie, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> findPostsByUser(User user, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> getAllPosts(Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> getFollowedUsersPosts(User user, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> getUserFavouritePosts(User user, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchPosts(String query, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchPostsByCategory(String query, String category, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchPostsOlderThan(String query, LocalDateTime fromDate, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchPostsByCategoryAndOlderThan(String query, String category, LocalDateTime fromDate, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<PostVote> getPostVotes(Post post, int pageNumber, int pageSize);
}