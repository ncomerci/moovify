package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceImplTest {

    private static final int UP_VOTE_VALUE = 1;
    private static final int DOWN_VOTE_VALUE = -1;

    @Mock
    private CommentDao dao;

    @Mock
    private MailService mailService;

    @InjectMocks
    private final CommentServiceImpl commentService = new CommentServiceImpl();

    /*
     * Se podria testear que el mail se mande con el locale del usuario.
     * Por ahora no se puede porque el Locale lo genera en el metodo
     * y no tenemos forma de controlar el output del constructor.
     */

    @Test
    public void testLikeCommentRemove() {

        Comment comment = Mockito.mock(Comment.class);

        commentService.likeComment(comment, Mockito.mock(User.class),0);

        Mockito.verify(comment).removeLike(Mockito.any());
    }

    @Test
    public void testLikeCommentGiveUpVote() {

        Comment comment = Mockito.mock(Comment.class);
        User user = Mockito.mock(User.class);

        commentService.likeComment(comment, user, UP_VOTE_VALUE);

        Mockito.verify(comment).like(Mockito.eq(user), Mockito.eq(UP_VOTE_VALUE));
    }

    @Test
    public void testLikeCommentGiveDownVote() {

        Comment comment = Mockito.mock(Comment.class);
        User user = Mockito.mock(User.class);

        commentService.likeComment(comment, user, DOWN_VOTE_VALUE);

        Mockito.verify(comment).like(Mockito.eq(user), Mockito.eq(DOWN_VOTE_VALUE));
    }
}
