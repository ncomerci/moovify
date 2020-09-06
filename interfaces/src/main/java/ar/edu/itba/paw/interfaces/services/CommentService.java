package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Comment;

import java.util.Optional;

public interface CommentService {

    Optional<Comment> findCommentById(long id);
}
