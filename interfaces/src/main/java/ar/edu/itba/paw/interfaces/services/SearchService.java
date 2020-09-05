package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Post;

import java.util.Set;

public interface SearchService {

    Set<Post> searchPosts(String searchParam);

    Set<Post> searchPostsbyTitle( String title );

    Set<Post> searchPostsbyMovieTitle( String movie_title );

    Set<Post> searchPostsbyMovieId( long movie_id );
}
