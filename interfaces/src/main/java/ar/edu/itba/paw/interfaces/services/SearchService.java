package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.Post;

import java.util.Collection;

public interface SearchService {

    Optional<Collection<Post>> searchPosts(String query, String category, String period, String sortCriteria);

    Collection<Movie> searchMovies(String query);
}
