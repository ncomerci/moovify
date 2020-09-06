package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Movie;

import java.time.LocalDate;
import java.util.Optional;

public interface CommentService {

    Optional<Comment> findCommentById(long id);

    Comment register(long postId, Long parentId, String body, String userMail);
}
