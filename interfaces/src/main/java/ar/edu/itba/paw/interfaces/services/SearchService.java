package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.Collection;

public interface SearchService {

    PaginatedCollection<Post> searchPosts(String query, String category, String period, String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Movie> searchMovies(String query, int pageNumber, int pageSize);

    PaginatedCollection<User> searchUsers(String query, String role, String sortCriteria, int pageNumber, int pageSize);

    Collection<String> getAllPostSortCriteria();

    Collection<String> getAllUserSortCriteria();

    Collection<String> getPostPeriodOptions();

    Collection<String> getPostCategories();

    Collection<String> getUserRoleOptions();
}
