package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Comment;

import java.util.Collection;
import java.util.Optional;

public interface CommentDao {

    long register(long postId, Long parentId, String body, long userId);

    Optional<Comment> findCommentByIdWithChildren(long id);

    Optional<Comment> findCommentByIdWithoutChildren(long id);

    Collection<Comment> findCommentsByPostIdWithChildren(long post_id);

    Collection<Comment> findCommentsByPostIdWithoutChildren(long post_id);
}
