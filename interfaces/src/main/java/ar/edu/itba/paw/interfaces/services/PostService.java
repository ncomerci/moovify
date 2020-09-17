package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategory;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface PostService {

    long register(String title, String email, String body, long category, Set<String> tags, Set<Long> movies);

    Optional<Post> findPostById(long id);

    Collection<Post> findPostsByMovieId(long movie_id);

    Collection<Post> getAllPostsOrderByNewest();

    Collection<Post> getAllPostsOrderByOldest();

    Collection<PostCategory> getAllPostCategories();
}