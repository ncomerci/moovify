package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.util.Collection;

public interface SearchService {

    PaginatedCollection<Post> searchPosts(String query, String category, String period, String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchDeletedPosts(String query, int pageNumber, int pageSize);

    PaginatedCollection<Movie> searchMovies(String query, String category, String decade, String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<User> searchUsers(String query, String role, String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> searchDeletedComments(String query, int pageNumber, int pageSize);

    PaginatedCollection<User> searchDeletedUsers(String query, int pageNumber, int pageSize);

    Collection<String> getAllPostSortCriteria();

    Collection<String> getAllMoviesSortCriteria();

    Collection<String> getAllUserSortCriteria();

    Collection<String> getPostPeriodOptions();

    Collection<String> getPostCategories();

    Collection<String> getMoviesCategories();

    Collection<String> getMoviesDecades();

    Collection<String> getUserRoleOptions();
}
