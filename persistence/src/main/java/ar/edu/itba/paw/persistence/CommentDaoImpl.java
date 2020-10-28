package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Repository
public class CommentDaoImpl implements CommentDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Comment register(Post post, Comment parent, String body, User user, boolean enabled) {

        final Comment comment = new Comment(LocalDateTime.now(), post, parent, Collections.emptyList(), body, user, enabled, Collections.emptyList());

        em.persist(comment);

        return comment;
    }

    @Override
    public Optional<Comment> findCommentById(long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public PaginatedCollection<Comment> findCommentChildren(Comment comment, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Comment> findCommentDescendants(Comment comment, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Comment> findPostCommentDescendants(Post post, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByPost(Post post, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByUser(User user, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Comment> getDeletedComments(SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public PaginatedCollection<Comment> searchDeletedComments(String query, SortCriteria sortCriteria, int pageNumber, int pageSize) {
        return null;
    }
}
