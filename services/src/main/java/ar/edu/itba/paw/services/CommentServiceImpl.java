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
                body.replaceAll("\\s+", " ").replaceAll("^ | $", ""), userId);
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
        return commentDao.findCommentsByPostIdWithChildren(post_id);
    }

    @Override
    public Collection<Comment> findCommentsByPostIdWithoutChildren(long post_id) {
        return commentDao.findCommentsByPostIdWithoutChildren(post_id);
    }
}
