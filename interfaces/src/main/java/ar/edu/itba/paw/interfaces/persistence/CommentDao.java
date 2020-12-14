package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.util.Optional;

public interface CommentDao {

    enum SortCriteria {
        NEWEST, OLDEST, HOTTEST
    }

    Comment register(Post post, Comment parent, String body, User user, boolean enabled);

    Optional<Comment> findCommentById(long id);

    PaginatedCollection<Comment> getAllComments(Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    /**
     * Get all direct children of a comment. Doesn't include sub children.
     */
    PaginatedCollection<Comment> findCommentChildren(Comment comment, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findPostChildrenComments(Post post, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByPost(Post post, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByUser(User user, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> searchComments(String query, Boolean enabled, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<CommentVote> getCommentVotes(Comment comment, int pageNumber, int pageSize);
}
