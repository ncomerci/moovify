package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface PostService {

    Post register(String title, String body, PostCategory category, User user, Set<String> tags, Set<Long> movies);

    void deletePost(Post post);

    void restorePost(Post post);

    void likePost(Post post, User user, int value);

    Optional<Post> findPostById(long id);

    Optional<Post> findDeletedPostById(long id);

    PaginatedCollection<Post> findPostsByMovie(Movie movie, int pageNumber, int pageSize);

    PaginatedCollection<Post> findPostsByUser(User user, int pageNumber, int pageSize);

    PaginatedCollection<Post> getAllPostsOrderByNewest(int pageNumber, int pageSize);

    PaginatedCollection<Post> getAllPostsOrderByOldest(int pageNumber, int pageSize);

    PaginatedCollection<Post> getAllPostsOrderByHottest(int pageNumber, int pageSize);

    Collection<PostCategory> getAllPostCategories();

    Optional<PostCategory> findCategoryById(long categoryId);
}