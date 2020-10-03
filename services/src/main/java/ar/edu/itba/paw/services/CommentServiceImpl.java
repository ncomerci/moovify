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
                        .replaceAll("^[ \r\n]+|[ \r\n]+$", ""), userId);
    }

    @Override
    public Optional<PaginatedCollection<Comment>> findCommentByIdWithChildren(long id, int pageNumber, int pageSize){
        return commentDao.findCommentByIdWithChildren(id, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public Optional<Comment> findCommentByIdWithoutChildren(long id){
        return commentDao.findCommentByIdWithoutChildren(id);
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByPostIdWithChildren(long post_id, int pageNumber, int pageSize) {
        return commentDao.findCommentsByPostIdWithChildren(post_id, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByPostIdWithoutChildren(long post_id, int pageNumber, int pageSize) {
        return commentDao.findCommentsByPostIdWithoutChildren(post_id, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }

    @Override
    public PaginatedCollection<Comment> findCommentsByUserIdWithoutChildren(long user_id, int pageNumber, int pageSize) {
        return commentDao.findCommentsByUserIdWithoutChildren(user_id, CommentDao.SortCriteria.NEWEST, pageNumber, pageSize);
    }
}
