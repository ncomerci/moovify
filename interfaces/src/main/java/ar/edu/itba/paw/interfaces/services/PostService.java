package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategory;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface PostService {

    long register(String title, String body, long category, long user, Set<String> tags, Set<Long> movies);

    void delete(long id);

    void likePost(long post_id, long user_id, boolean value);

    Optional<Post> findPostById(long id);

    PaginatedCollection<Post> findPostsByMovieId(long movie_id, int pageNumber, int pageSize);

    PaginatedCollection<Post> findPostsByUserId(long user_id, int pageNumber, int pageSize);

    PaginatedCollection<Post> getAllPostsOrderByNewest(int pageNumber, int pageSize);

    PaginatedCollection<Post> getAllPostsOrderByOldest(int pageNumber, int pageSize);

    PaginatedCollection<Post> getAllPostsOrderByHottest(int pageNumber, int pageSize);

    PaginatedCollection<Post> getDeletedPosts(int pageNumber, int pageSize);

    Collection<PostCategory> getAllPostCategories();
}