package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Comment;

import java.util.Collection;
import java.util.Optional;

public interface CommentDao {

    enum SortCriteria {
        NEWEST, OLDEST, HOTTEST
    }

    long register(long postId, Long parentId, String body, long userId);

    void likeComment(long comment_id, long user_id);

    void removeLike(long comment_id, long user_id);

    Optional<Comment> findCommentByIdWithChildren(long id);

    Optional<Comment> findCommentByIdWithoutChildren(long id);

    Collection<Comment> findCommentsByPostIdWithChildren(long post_id, SortCriteria sortCriteria);

    Collection<Comment> findCommentsByPostIdWithoutChildren(long post_id, SortCriteria sortCriteria);

    Collection<Comment> findCommentsByUserIdWithChildren(long user_id, SortCriteria sortCriteria);

    Collection<Comment> findCommentsByUserIdWithoutChildren(long user_id, SortCriteria sortCriteria);
}
