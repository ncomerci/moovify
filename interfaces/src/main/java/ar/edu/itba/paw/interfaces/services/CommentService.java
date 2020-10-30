package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface CommentService {

    Comment register(Post post, Comment parent, String body, User user, String mailTemplate);

    void likeComment(Comment comment, User user, int value);

    void deleteComment(Comment comment);

    void restoreComment(Comment comment);

    Optional<Comment> findCommentById(long id);

    PaginatedCollection<Comment> findCommentChildren(Comment comment, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentDescendants(Comment comment, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findPostCommentDescendants(Post post, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByPost(Post post, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByUser(User user, int pageNumber, int pageSize);

    int getMaxCommentTreeDepth();
}
