package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.exceptions.IllegalCommentEditionException;
import ar.edu.itba.paw.interfaces.services.exceptions.IllegalCommentLikeException;
import ar.edu.itba.paw.interfaces.services.exceptions.MissingCommentEditPermissionException;
import ar.edu.itba.paw.interfaces.services.exceptions.RestoredEnabledModelException;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.Collection;
import java.util.Optional;

public interface CommentService {

    Comment register(Post post, Comment parent, String body, User user, String mailTemplate);

    void editComment(User editor, Comment comment, String newBody) throws IllegalCommentEditionException, MissingCommentEditPermissionException;

    void likeComment(Comment comment, User user, int value) throws IllegalCommentLikeException;

    void deleteComment(Comment comment);

    void restoreComment(Comment comment) throws RestoredEnabledModelException;

    Optional<Comment> findCommentById(long id);

    PaginatedCollection<Comment> findCommentChildren(Comment comment, String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentDescendants(Comment comment, String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findPostCommentDescendants(Post post, String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByPost(Post post, String sortCriteria, int pageNumber, int pageSize);

    PaginatedCollection<Comment> findCommentsByUser(User user, String sortCriteria, int pageNumber, int pageSize);

    long getMaxCommentTreeDepth();

    CommentDao.SortCriteria getCommentSortCriteria(String sortCriteriaName);

    Collection<String> getCommentSortOptions();
}
