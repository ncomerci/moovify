package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Post;

import java.util.Collection;

public interface SearchService {

    Collection<Post> findPostsByPostAndMovieTitle(String title, boolean withMovies);

    Collection<Post> findPostsByTitle(String title, boolean withMovies);

    Collection<Post> findPostsByMovieTitle(String movie_title, boolean withMovies);

    Collection<Post> findPostsByMovieId(long movie_id, boolean withMovies);
}
