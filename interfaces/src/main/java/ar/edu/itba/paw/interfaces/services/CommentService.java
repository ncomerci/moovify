package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;

import java.util.Collection;
import java.util.Optional;

public interface CommentService {

    long register(long postId, Long parentId, String body, long userId);

    void likeComment(long comment_id, long user_id, boolean value);

    void delete(long id);

    void restore(long id);

    Optional<Comment> findCommentById(long id);

    PaginatedCollection<Comment> findCommentChildren(long commentId, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentDescendants(long commentId, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findPostCommentDescendants(long post_id, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByPostId(long post_id, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByUserId(long user_id, int pageNumber, int pageSize);

    PaginatedCollection<Comment> getDeletedComments(int pageNumber, int pageSize);
}
