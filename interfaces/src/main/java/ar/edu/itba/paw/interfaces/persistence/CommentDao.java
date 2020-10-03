package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;

import java.util.Optional;

public interface CommentDao {

    enum SortCriteria {
        NEWEST, OLDEST
    }

    long register(long postId, Long parentId, String body, long userId);

    Optional<PaginatedCollection<Comment>> findCommentByIdWithChildren(long id, SortCriteria sortCriteria, int pageNumber, int pageSize);

    Optional<Comment> findCommentByIdWithoutChildren(long id);

    PaginatedCollection<Comment> findCommentsByPostIdWithChildren(long post_id, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByPostIdWithoutChildren(long post_id, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByUserIdWithoutChildren(long user_id, SortCriteria sortCriteria, int pageNumber, int pageSize);
}
