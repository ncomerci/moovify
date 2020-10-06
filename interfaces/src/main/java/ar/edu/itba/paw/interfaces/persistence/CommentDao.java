package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;

import java.util.Optional;

public interface CommentDao {

    enum SortCriteria {
        NEWEST, OLDEST, HOTTEST
    }

    long register(long postId, Long parentId, String body, long userId, boolean enabled);

    void likeComment(long comment_id, long user_id, int value);

    void removeLike(long comment_id, long user_id);

    void delete(long id);

    Optional<Comment> findCommentById(long id);

    PaginatedCollection<Comment> findCommentChildren(long commentId, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentDescendants(long commentId, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findPostCommentDescendants(long post_id, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByPostId(long post_id, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByUserId(long user_id, SortCriteria sortCriteria, int pageNumber, int pageSize);
}
