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
    public Optional<Comment> findCommentById(long id){
        return commentDao.findCommentById(id);
    }

    @Override
    public Comment register(long postId, Long parentId, String body, String userMail) {
        return commentDao.register(postId, parentId, body, userMail);
    }

    @Override
    public Collection<Comment> findCommentsByPostId(long post_id){
        return commentDao.findCommentsByPostId(post_id);
    }
}
