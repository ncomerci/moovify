package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);

    // Max Depth The Comment Tree Has At Render Time
    private static final Long MAX_COMMENT_TREE_DEPTH = 3L;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private MailService mailService;

    @Autowired
    private MessageSource messageSource;

    @Transactional
    @Override
    public Comment register(Post post, Comment parent, String body, User user, String mailTemplate) {

        final Comment comment = commentDao.register(post, parent,
                body.trim().replaceAll("[ \t]+", " ")
                        .replaceAll("(\r\n)+", "\n")
                        .replaceAll("^[ \r\n]+|[ \r\n]+$", ""), user, true);

        final Map<String, Object> map = new HashMap<>();
        map.put("post", post);
        map.put("comment", comment);

        final Locale mailLocale = new Locale(post.getUser().getLanguage());

        mailService.sendEmail(post.getUser().getEmail(),
                messageSource.getMessage("mail.newComment.subject", new Object[]{ post.getTitle() }, mailLocale), mailTemplate, map, mailLocale);

        LOGGER.info("Created Comment: {}", comment.getId());

        return comment;
    }

    @Transactional
    @Override
    public void editComment(Comment comment, String newBody) {

        comment.setBody(
                newBody.trim().replaceAll("[ \t]+", " ")
                .replaceAll("(\r\n)+", "\n")
                .replaceAll("^[ \r\n]+|[ \r\n]+$", "")
        );
    }

    @Transactional
    @Override
    public void likeComment(Comment comment, User user, int value) {

        if(value == 0) {
            LOGGER.info("Delete Like: User {} Comment {}", user.getId(), comment.getId());
            comment.removeLike(user);
        }

        else if(value == -1 || value == 1) {
            LOGGER.info("Like: User {} Comment {} Value {}", user.getId(), comment.getId(), value);
            comment.like(user, value);
        }
    }

    @Transactional
    @Override
    public void deleteComment(Comment comment) {
        LOGGER.info("Delete Comment {}", comment.getId());
        comment.setEnabled(false);
    }

    @Transactional
    @Override
    public void restoreComment(Comment comment) {
        LOGGER.info("Restore Comment {}", comment.getId());
        comment.setEnabled(true);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Comment> findCommentById(long commentId) {
        return commentDao.findCommentById(commentId);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Comment> findCommentChildren(Comment comment, int pageNumber, int pageSize) {
        return commentDao.findCommentChildren(comment, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Comment> findCommentDescendants(Comment comment, int pageNumber, int pageSize) {
        return commentDao.findCommentDescendants(comment, MAX_COMMENT_TREE_DEPTH, CommentDao.SortCriteria.HOTTEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Comment> findPostCommentDescendants(Post post, int pageNumber, int pageSize) {
        return commentDao.findPostCommentDescendants(post, MAX_COMMENT_TREE_DEPTH, CommentDao.SortCriteria.HOTTEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Comment> findCommentsByPost(Post post, int pageNumber, int pageSize) {
        return commentDao.findCommentsByPost(post, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Comment> findCommentsByUser(User user, int pageNumber, int pageSize) {
        return commentDao.findCommentsByUser(user, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public long getMaxCommentTreeDepth() {
        return MAX_COMMENT_TREE_DEPTH;
    }
}
