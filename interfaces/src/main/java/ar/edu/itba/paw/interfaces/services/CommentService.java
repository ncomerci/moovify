package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface CommentService {

    long register(Post post, Long parentId, String body, User user, String mailTemplate);

    void likeComment(Comment comment, User user, int value);

    void delete(long id);

    Optional<Comment> findCommentById(long id);

    PaginatedCollection<Comment> findCommentChildren(long commentId, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentDescendants(long commentId, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findPostCommentDescendants(long post_id, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByPostId(long post_id, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByUserId(long user_id, int pageNumber, int pageSize);
}
