package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;

import java.util.Optional;

public interface CommentService {

    long register(long postId, Long parentId, String body, long userId);

    Optional<Comment> findCommentByIdWithChildren(long id);

    Optional<Comment> findCommentByIdWithoutChildren(long id);

    PaginatedCollection<Comment> findCommentsByPostIdWithChildren(long post_id, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByPostIdWithoutChildren(long post_id, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByUserIdWithChildren(long user_id, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByUserIdWithoutChildren(long user_id, int pageNumber, int pageSize);
}
