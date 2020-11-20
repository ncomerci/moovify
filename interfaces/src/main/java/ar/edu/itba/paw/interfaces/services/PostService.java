package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.exceptions.*;
import ar.edu.itba.paw.models.*;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface PostService {

    Post register(String title, String body, PostCategory category, User user, Set<String> tags, Set<Long> movies);

    void deletePost(Post post) throws DeletedDisabledModelException;

    void restorePost(Post post) throws RestoredEnabledModelException;

    void likePost(Post post, User user, int value) throws IllegalPostLikeException;

    void editPost(User editor, Post post, String newBody) throws MissingPostEditPermissionException, IllegalPostEditionException;

    void guaranteePostEditionPermissions(User editor, Post post) throws IllegalPostEditionException, MissingPostEditPermissionException;

    Optional<Post> findPostById(long id);

    Optional<Post> findDeletedPostById(long id);

    PaginatedCollection<Post> findPostsByMovie(Movie movie, int pageNumber, int pageSize);

    PaginatedCollection<Post> findPostsByUser(User user, int pageNumber, int pageSize);

    PaginatedCollection<Post> getAllPostsOrderByNewest(int pageNumber, int pageSize);

    PaginatedCollection<Post> getAllPostsOrderByOldest(int pageNumber, int pageSize);

    PaginatedCollection<Post> getAllPostsOrderByHottest(int pageNumber, int pageSize);

    PaginatedCollection<Post> getFollowedUsersPosts(User user, int pageNumber, int pageSize);

    PaginatedCollection<Post> getUserFavouritePosts(User user, int pageNumber, int pageSize);

    Collection<PostCategory> getAllPostCategories();

    Optional<PostCategory> findCategoryById(long categoryId);
}