package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.exceptions.IllegalCommentEditionException;
import ar.edu.itba.paw.interfaces.services.exceptions.IllegalCommentLikeException;
import ar.edu.itba.paw.interfaces.services.exceptions.MissingCommentEditPermissionException;
import ar.edu.itba.paw.interfaces.services.exceptions.RestoredEnabledModelException;
import ar.edu.itba.paw.models.*;

import java.util.Collection;
import java.util.Optional;

public interface CommentService {

    Comment register(Post post, Comment parent, String body, User user, String mailTemplate);

    void editComment(User editor, Comment comment, String newBody) throws IllegalCommentEditionException, MissingCommentEditPermissionException;

    void likeComment(Comment comment, User user, int value) throws IllegalCommentLikeException;

    void deleteComment(Comment comment);

    void restoreComment(Comment comment) throws RestoredEnabledModelException;

    int getVoteValue(Comment comment, User user);

    Optional<Comment> findCommentById(long id);

    PaginatedCollection<Comment> getAllComments(Boolean enabled, String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentChildren(Comment comment, Boolean enabled, String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findPostChildrenComments(Post post, Boolean enabled, String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByPost(Post post, Boolean enabled, String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByUser(User user, Boolean enabled, String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<CommentVote> getCommentVotes(Comment comment, int pageNumber, int pageSize);

    CommentDao.SortCriteria getCommentSortCriteria(String sortCriteriaName);

    Collection<String> getCommentSortOptions();
}
