package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.Collection;
import java.util.Optional;

public interface SearchService {

    /**
     * Search methods required a query, at least an empty string.
     * pageSize should be > 0 and pageNumber >= 0.
     * A valid sortCriteria name for the entity must be provided.
     * Every other field can be null and the default behaviour will be used.
     */

    Optional<PaginatedCollection<Post>> searchPosts(String query, String category, String period, Boolean enabled, String sortCriteria, int pageNumber, int pageSize);

    Optional<PaginatedCollection<Movie>> searchMovies(String query, String category, String decade, String sortCriteria, int pageNumber, int pageSize);

    Optional<PaginatedCollection<User>> searchUsers(String query, String role, Boolean enabled, String sortCriteria, int pageNumber, int pageSize);

    /**
     * Available options for search methods
     */

    Collection<String> getPostPeriodOptions();

    Collection<String> getPostCategories();

    Collection<String> getMoviesCategories();

    Collection<String> getMoviesDecades();

    Collection<String> getUserRoleOptions();
}
