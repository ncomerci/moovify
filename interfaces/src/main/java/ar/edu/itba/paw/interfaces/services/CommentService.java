package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Comment;

import java.util.Collection;
import java.util.Optional;

public interface CommentService {

    long register(long postId, Long parentId, String body, long userId);

    Optional<Comment> findCommentByIdWithChildren(long id);

    Optional<Comment> findCommentByIdWithoutChildren(long id);

    Collection<Comment> findCommentsByPostIdWithChildren(long post_id);

    Collection<Comment> findCommentsByPostIdWithoutChildren(long post_id);

    Collection<Comment> findCommentsByUserIdWithChildren(long user_id);

    Collection<Comment> findCommentsByUserIdWithoutChildren(long user_id);

}
