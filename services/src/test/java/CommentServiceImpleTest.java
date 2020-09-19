import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.services.CommentServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceImpleTest {
    private static final long ID = 1;

    @Mock
    private CommentDao dao;

    @InjectMocks
    private final CommentServiceImpl commentService = new CommentServiceImpl();

    @Test
    public void testfindCommentById() {
//        1. Setup: Establezco las pre-condiociones
        Optional<Comment> optional = Optional.empty();
        Mockito.when(dao.findCommentById(Mockito.eq(ID), Mockito.eq(false)))
                .thenReturn(optional);

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        Optional<Comment> comment = commentService.findCommentById(1, false);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertNotNull(comment);
        Assert.assertFalse(comment.isPresent());
    }

    @Test
    public void testRegister() {
//        1. Setup: Establezco las pre-condiociones
        Mockito.when(dao.register(Mockito.eq(ID), Mockito.eq(null), Mockito.eq("comment body"), Mockito.eq("email")))
                .thenReturn(Mockito.mock(Comment.class));

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        Comment comment = commentService.register(ID, null, "comment body", "email");

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertNotNull(comment);
    }

    @Test
    public void testfindCommentsByPostId() {
//        1. Setup: Establezco las pre-condiociones
        final ArrayList<Comment> list = new ArrayList<>();
        list.add(Mockito.mock(Comment.class));
        list.add(Mockito.mock(Comment.class));
        list.add(Mockito.mock(Comment.class));

        Mockito.when(dao.findCommentsByPostId(Mockito.eq(ID), Mockito.eq(false)))
                .thenReturn(list);

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        Collection<Comment> comments = commentService.findCommentsByPostId(1, false);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertNotNull(comments);
        Assert.assertEquals(3, comments.size());
    }
}
