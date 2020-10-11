package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface CommentDao {

    enum SortCriteria {
        NEWEST, OLDEST, HOTTEST
    }

    Comment register(Post post, Comment parent, String body, User user, boolean enabled);

    void likeComment(Comment comment, User user, int value);

    void removeLike(Comment comment, User user);

    void deleteComment(Comment comment);

    void restoreComment(Comment comment);

    Optional<Comment> findCommentById(long id);

    PaginatedCollection<Comment> findCommentChildren(Comment comment, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentDescendants(Comment comment, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findPostCommentDescendants(Post post, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByPost(Post post, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByUser(User user, SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> getDeletedComments(SortCriteria sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> searchDeletedComments(String query, SortCriteria sortCriteria, int pageNumber, int pageSize);
}
