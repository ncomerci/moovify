package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.models.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
    public Optional<Comment> findCommentByIdWithChildren(long id){
        return commentDao.findCommentByIdWithChildren(id);
    }

    @Override
    public Optional<Comment> findCommentByIdWithoutChildren(long id){
        return commentDao.findCommentByIdWithoutChildren(id);
    }

    @Override
    public Collection<Comment> findCommentsByPostIdWithChildren(long post_id) {
        return commentDao.findCommentsByPostIdWithChildren(post_id, CommentDao.SortCriteria.NEWEST);
    }

    @Override
    public Collection<Comment> findCommentsByPostIdWithoutChildren(long post_id) {
        return commentDao.findCommentsByPostIdWithoutChildren(post_id, CommentDao.SortCriteria.NEWEST);
    }

    @Override
    public Collection<Comment> findCommentsByUserIdWithChildren(long user_id) {
        return commentDao.findCommentsByUserIdWithChildren(user_id, CommentDao.SortCriteria.NEWEST);
    }

    @Override
    public Collection<Comment> findCommentsByUserIdWithoutChildren(long user_id) {
        return commentDao.findCommentsByUserIdWithoutChildren(user_id, CommentDao.SortCriteria.NEWEST);
    }
}
