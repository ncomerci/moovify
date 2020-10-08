package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private MailService mailService;

    @Transactional
    @Override
    public Comment register(Post post, Comment parent, String body, User user, String mailTemplate) {

        final Comment comment = commentDao.register(post, parent,
                body.trim().replaceAll("[ \t]+", " ")
                        .replaceAll("(\r\n)+", "\n")
                        .replaceAll("^[ \r\n]+|[ \r\n]+$", ""), user, true);

        Map<String, Object> map = new HashMap<>();

        map.put("post", post);
        map.put("comment", comment);

        mailService.sendEmail(post.getUser().getEmail(),
                "New comment on your post " + post.getTitle(), mailTemplate, map);

        return comment;
    }

    @Transactional
    @Override
    public void likeComment(Comment comment, User user, int value) {

        if(value == 0)
            commentDao.removeLike(comment, user);

        else if(value == -1 || value == 1)
            commentDao.likeComment(comment, user, value);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentDao.deleteComment(comment);
    }

    @Override
    public void restoreComment(Comment comment) {
        commentDao.restoreComment(comment);
    }

    @Override
    public Optional<Comment> findCommentById(long commentId) {
        return commentDao.findCommentById(commentId);
    }

    @Override
    public PaginatedCollection<Comment> findCommentChildren(Comment comment, int pageNumber, int pageSize) {
        return commentDao.findCommentChildren(comment, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Comment> findCommentDescendants(Comment comment, int pageNumber, int pageSize) {
        return commentDao.findCommentDescendants(comment, CommentDao.SortCriteria.HOTTEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Comment> findPostCommentDescendants(Post post, int pageNumber, int pageSize) {
        return commentDao.findPostCommentDescendants(post, CommentDao.SortCriteria.HOTTEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByPost(Post post, int pageNumber, int pageSize) {
        return commentDao.findCommentsByPost(post, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByUser(User user, int pageNumber, int pageSize) {
        return commentDao.findCommentsByUser(user, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }
}
