package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDao commentDao;

    @Override
    public long register(long postId, Long parentId, String body, long userId) {
        return commentDao.register(postId, parentId,
                body.trim().replaceAll("[ \t]+", " ")
                        .replaceAll("(\r\n)+", "\n")
                        .replaceAll("^[ \r\n]+|[ \r\n]+$", ""), userId, true);
    }

    @Override
    public void likeComment(long comment_id, long user_id, boolean value) {
        if(value)
            commentDao.likeComment(comment_id, user_id);
        else
            commentDao.removeLike(comment_id, user_id);
    }

    @Override
    public void delete(long id) { commentDao.delete(id); }

    @Override
    public Optional<Comment> findCommentById(long commentId){
        return commentDao.findCommentById(commentId);
    }

    @Override
    public PaginatedCollection<Comment> findCommentChildren(long commentId, int pageNumber, int pageSize) {
        return commentDao.findCommentChildren(commentId, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Comment> findCommentDescendants(long commentId, int pageNumber, int pageSize) {
        return commentDao.findCommentDescendants(commentId, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Comment> findPostCommentDescendants(long post_id, int pageNumber, int pageSize) {
        return commentDao.findPostCommentDescendants(post_id, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByPostId(long post_id, int pageNumber, int pageSize) {
        return commentDao.findCommentsByPostId(post_id, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByUserId(long user_id, int pageNumber, int pageSize) {
        return commentDao.findCommentsByUserId(user_id, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Comment> getDeletedComments(int pageNumber, int pageSize) {
        return commentDao.getDeletedComments(CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }
}
