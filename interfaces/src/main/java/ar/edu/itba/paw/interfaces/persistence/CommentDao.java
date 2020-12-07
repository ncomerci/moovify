package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.util.Optional;

public interface CommentDao {

    enum SortCriteria {
        NEWEST, OLDEST, HOTTEST
    }

    Comment register(Post post, Comment parent, String body, User user, boolean enabled);

    int getVoteValue(Comment comment, User user);

    Optional<Comment> findCommentById(long id);

    /**
     * Get all direct children of a comment. Doesn't include sub children.
     */
    PaginatedCollection<Comment> findCommentChildren(Comment comment, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentDescendants(Comment comment, long maxDepth, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findPostCommentDescendants(Post post, long maxDepth, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByPost(Post post, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByUser(User user, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> getDeletedComments(SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> searchDeletedComments(String query, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<CommentLike> getCommentVotes(Comment comment, String sortCriteria, int pageNumber, int pageSize);
}
