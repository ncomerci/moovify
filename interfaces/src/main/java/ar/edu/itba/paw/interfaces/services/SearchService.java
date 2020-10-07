package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

public interface SearchService {

    PaginatedCollection<Post> searchPosts(String query, String category, String period, String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Post> searchDeletedPosts(String query, int pageNumber, int pageSize);

    PaginatedCollection<Movie> searchMovies(String query, int pageNumber, int pageSize);

    PaginatedCollection<User> searchUsers(String query, int pageNumber, int pageSize);

    PaginatedCollection<User> searchDeletedUsers(String query, int pageNumber, int pageSize);

    PaginatedCollection<Comment> searchDeletedComments(String query, int pageNumber, int pageSize);

}
