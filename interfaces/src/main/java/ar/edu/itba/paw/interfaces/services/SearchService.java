package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.Collection;
import java.util.Optional;

public interface SearchService {

    Optional<PaginatedCollection<Post>> searchPosts(String query, String category, String period, String sortCriteria, int pageNumber, int pageSize);

    Optional<Collection<Movie>> searchMovies(String query);

    Optional<Collection<User>> searchUsers(String query);
}
