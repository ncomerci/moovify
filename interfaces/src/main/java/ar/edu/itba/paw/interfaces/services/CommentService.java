package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Comment;

import java.util.Collection;
import java.util.Optional;

public interface CommentService {

    Comment register(long postId, Long parentId, String body, String userMail);

    Optional<Comment> findCommentById(long id, boolean withChildren);

    Collection<Comment> findCommentsByPostId(long post_id, boolean withChildren);
}
