package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Comment;

import java.util.Collection;
import java.util.Optional;

public interface CommentDao {

    Comment register(long postId, Long parentId, String body, String userMail);

    Optional<Comment> findCommentById(long id);

    Collection<Comment> findCommentsByPostId(long post_id);
}
