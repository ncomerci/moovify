package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Post;

import java.util.Collection;

public interface SearchService {

    Collection<Post> searchPostsbyTitle( String title );

    Collection<Post> searchPostsbyMovieTitle( String movie_title );

    Collection<Post> searchPostsbyMovieId(long movie_id );
}
