package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.PaginatedCollection;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceImplTest {

    private static final String BODY = "comment body";
    private static final long ID = 1L;
    private static final int PAGE_NUMBER = 1;
    private static final int PAGE_SIZE = 1;
    private static final int TOTAL_COUNT = 1;

    @Mock
    private CommentDao dao;

    @Mock
    private MailService mailService;

    @InjectMocks
    private final CommentServiceImpl commentService = new CommentServiceImpl();

    @Test
    public void testRegister() {
//        1. Setup: Establezco las pre-condiociones

        Comment comment = Mockito.mock(Comment.class);
        Post post = Mockito.mock(Post.class);
        User user = Mockito.mock(User.class);

        Mockito.when(dao.register(Mockito.eq(post), Mockito.eq(null), Mockito.eq(BODY), Mockito.eq(user), Mockito.eq(true)))
                .thenReturn(comment);
        Mockito.when(post.getUser()).thenReturn(user);

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        Comment commentResult = commentService.register(post, null, BODY, user,null, Locale.ENGLISH);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertNotNull(commentResult);
        Assert.assertEquals(comment, commentResult);

    }

    @Test
    public void testLikeCommentRemove() {

        commentService.likeComment(Mockito.mock(Comment.class), Mockito.mock(User.class),0);
    }

    @Test
    public void testLikeCommentGiveUpVote() {

        commentService.likeComment(Mockito.mock(Comment.class), Mockito.mock(User.class),1);
    }

    @Test
    public void testLikeCommentGiveDownVote() {

        commentService.likeComment(Mockito.mock(Comment.class), Mockito.mock(User.class),-1);
    }

    @Test
    public void testDeleteComment() {
        commentService.deleteComment(Mockito.mock(Comment.class));
    }

    @Test
    public void testRestoreComment() {
        commentService.restoreComment(Mockito.mock(Comment.class));
    }

    @Test
    public void testFindCommentById() {
//        1. Setup: Establezco las pre-condiociones
        Comment comment = Mockito.mock(Comment.class);
        Optional<Comment> optional = Optional.of(comment);
        Mockito.when(dao.findCommentById(Mockito.eq(ID))).thenReturn(optional);

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        Optional<Comment> commentOptional = commentService.findCommentById(ID);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertTrue(commentOptional.isPresent());
        Assert.assertEquals(comment, commentOptional.get());
    }

    @Test
    public void testFindCommentChildren() {
//        1. Setup: Establezco las pre-condiociones
        Comment comment = Mockito.mock(Comment.class);
        Collection<Comment> collection = Collections.singleton(comment);
        PaginatedCollection<Comment> pCollection = new PaginatedCollection<>(collection, PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT);

        Mockito.when(dao.findCommentChildren(Mockito.eq(comment), Mockito.eq(CommentDao.SortCriteria.NEWEST), Mockito.eq(PAGE_NUMBER), Mockito.eq(PAGE_SIZE)))
                .thenReturn(pCollection);

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        PaginatedCollection<Comment> commentCollection = commentService.findCommentChildren(comment, PAGE_NUMBER, PAGE_SIZE);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertNotNull(commentCollection);
        Assert.assertEquals(collection, commentCollection.getResults());
    }

    @Test
    public void testFindCommentDescendants() {
//        1. Setup: Establezco las pre-condiociones
        Comment comment = Mockito.mock(Comment.class);
        Collection<Comment> collection = Collections.singleton(comment);
        PaginatedCollection<Comment> pCollection = new PaginatedCollection<>(collection, PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT);

        Mockito.when(dao.findCommentDescendants(Mockito.eq(comment), 5, Mockito.eq(CommentDao.SortCriteria.HOTTEST), Mockito.eq(PAGE_NUMBER), Mockito.eq(PAGE_SIZE)))
                .thenReturn(pCollection);

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        PaginatedCollection<Comment> commentCollection = commentService.findCommentDescendants(comment, PAGE_NUMBER, PAGE_SIZE);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertNotNull(commentCollection);
        Assert.assertEquals(collection, commentCollection.getResults());
    }

    @Test
    public void testFindPostCommentDescendants() {
//        1. Setup: Establezco las pre-condiociones
        Post post = Mockito.mock(Post.class);
        Comment comment = Mockito.mock(Comment.class);
        Collection<Comment> collection = Collections.singleton(comment);
        PaginatedCollection<Comment> pCollection = new PaginatedCollection<>(collection, PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT);

        Mockito.when(dao.findPostCommentDescendants(Mockito.eq(post), 5, Mockito.eq(CommentDao.SortCriteria.HOTTEST), Mockito.eq(PAGE_NUMBER), Mockito.eq(PAGE_SIZE)))
                .thenReturn(pCollection);

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        PaginatedCollection<Comment> commentCollection = commentService.findPostCommentDescendants(post, PAGE_NUMBER, PAGE_SIZE);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertNotNull(commentCollection);
        Assert.assertEquals(collection, commentCollection.getResults());
    }

    @Test
    public void testCommentsByPost() {
//        1. Setup: Establezco las pre-condiociones
        Post post = Mockito.mock(Post.class);
        Comment comment = Mockito.mock(Comment.class);
        Collection<Comment> collection = Collections.singleton(comment);
        PaginatedCollection<Comment> pCollection = new PaginatedCollection<>(collection, PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT);

        Mockito.when(dao.findCommentsByPost(Mockito.eq(post), Mockito.eq(CommentDao.SortCriteria.NEWEST), Mockito.eq(PAGE_NUMBER), Mockito.eq(PAGE_SIZE)))
                .thenReturn(pCollection);

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        PaginatedCollection<Comment> commentCollection = commentService.findCommentsByPost(post, PAGE_NUMBER, PAGE_SIZE);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertNotNull(commentCollection);
        Assert.assertEquals(collection, commentCollection.getResults());
    }

    @Test
    public void testFindCommentsByUser() {
//        1. Setup: Establezco las pre-condiociones
        User user = Mockito.mock(User.class);
        Comment comment = Mockito.mock(Comment.class);
        Collection<Comment> collection = Collections.singleton(comment);
        PaginatedCollection<Comment> pCollection = new PaginatedCollection<>(collection, PAGE_NUMBER, PAGE_SIZE, TOTAL_COUNT);

        Mockito.when(dao.findCommentsByUser(Mockito.eq(user), Mockito.eq(CommentDao.SortCriteria.NEWEST), Mockito.eq(PAGE_NUMBER), Mockito.eq(PAGE_SIZE)))
                .thenReturn(pCollection);

//        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
        PaginatedCollection<Comment> commentCollection = commentService.findCommentsByUser(user, PAGE_NUMBER, PAGE_SIZE);

//        3. Validaciones: Confirmo las postcondiciones
        Assert.assertNotNull(commentCollection);
        Assert.assertEquals(collection, commentCollection.getResults());
    }
}
