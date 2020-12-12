package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.exceptions.*;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private MailService mailService;

    @Autowired
    private MessageSource messageSource;

    private final static Map<String, CommentDao.SortCriteria> sortCriteriaMap = initializeSortCriteriaMap();

    private static Map<String, CommentDao.SortCriteria> initializeSortCriteriaMap() {
        final Map<String, CommentDao.SortCriteria> sortCriteriaMap = new LinkedHashMap<>();

        sortCriteriaMap.put("newest", CommentDao.SortCriteria.NEWEST);
        sortCriteriaMap.put("oldest", CommentDao.SortCriteria.OLDEST);
        sortCriteriaMap.put("hottest", CommentDao.SortCriteria.HOTTEST);

        return sortCriteriaMap;
    }

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
    public void editComment(User editor, Comment comment, String newBody) throws IllegalCommentEditionException, MissingCommentEditPermissionException {

        if(!comment.isEnabled())
            throw new IllegalCommentEditionException();

        if(!editor.equals(comment.getUser()))
            throw new MissingCommentEditPermissionException();

        comment.setBody(
                newBody.trim().replaceAll("[ \t]+", " ")
                .replaceAll("(\r\n)+", "\n")
                .replaceAll("^[ \r\n]+|[ \r\n]+$", "")
        );
    }

    @Transactional
    @Override
    public void likeComment(Comment comment, User user, int value) throws IllegalCommentLikeException {

        if(!comment.isEnabled())
            throw new IllegalCommentLikeException();

        if(comment.getVoteValue(user) == value)
            return;

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
    public void restoreComment(Comment comment) throws RestoredEnabledModelException {

        if(comment.isEnabled())
            throw new RestoredEnabledModelException();

        LOGGER.info("Restore Comment {}", comment.getId());

        comment.setEnabled(true);
    }

    @Override
    public int getVoteValue(Comment comment, User user) {
        return comment.getVoteValue(user);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Comment> findCommentById(long commentId) {
        return commentDao.findCommentById(commentId);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Comment> getAllComments(Boolean enabled, String sortCriteria, int pageNumber, int pageSize) {
        return commentDao.getAllComments(enabled, getCommentSortCriteria(sortCriteria), pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Comment> findCommentChildren(Comment comment, Boolean enabled, String sortCriteria, int pageNumber, int pageSize) {
        return commentDao.findCommentChildren(comment, enabled, getCommentSortCriteria(sortCriteria), pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Comment> findPostChildrenComments(Post post, Boolean enabled, String sortCriteria, int pageNumber, int pageSize) {
        return commentDao.findPostChildrenComments(post, enabled, getCommentSortCriteria(sortCriteria), pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Comment> findCommentsByPost(Post post, Boolean enabled, String sortCriteria, int pageNumber, int pageSize) {
        return commentDao.findCommentsByPost(post, enabled, getCommentSortCriteria(sortCriteria), pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedCollection<Comment> findCommentsByUser(User user, Boolean enabled, String sortCriteria, int pageNumber, int pageSize) {
        return commentDao.findCommentsByUser(user, enabled, getCommentSortCriteria(sortCriteria), pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<CommentVote> getCommentVotes(Comment comment, int pageNumber, int pageSize) {
        return commentDao.getCommentVotes(comment, pageNumber, pageSize);
    }

    @Override
    public CommentDao.SortCriteria getCommentSortCriteria(String sortCriteriaName) {
        if (sortCriteriaName != null && sortCriteriaMap.containsKey(sortCriteriaName))
            return sortCriteriaMap.get(sortCriteriaName);

        else
            throw new InvalidSortCriteriaException();
    }

    @Override
    public Collection<String> getCommentSortOptions() {
        return sortCriteriaMap.keySet();
    }
}
