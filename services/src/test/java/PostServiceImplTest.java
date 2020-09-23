//import ar.edu.itba.paw.interfaces.persistence.PostCategoryDao;
//import ar.edu.itba.paw.interfaces.persistence.PostDao;
//import ar.edu.itba.paw.models.Post;
//import ar.edu.itba.paw.models.PostCategory;
//import ar.edu.itba.paw.services.PostServiceImpl;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.util.*;
//
//@RunWith(MockitoJUnitRunner.Silent.class)
//public class PostServiceImplTest {
//    private static final Long CATEGORY_ID = 1L;
//    private static final String TITLE = "POST TEST";
//    private static final Long USER_ID = 1L;
//    private static final String BODY = "testing";
//    private static final Set<Long> MOVIES = new HashSet<>(Collections.singletonList(1L));
//
//    @Mock
//    private PostDao dao;
//
//    @Mock
//    private PostCategoryDao categoryDao;
//
//    @InjectMocks
//    private final PostServiceImpl postService = new PostServiceImpl();
//
//    // TODO: Rehacer test
//
////    @Test
////    public void testRegister() {
//////        1. Setup: Establezco las pre-condiociones
////        Mockito.when(dao.register(Mockito.eq(TITLE), Mockito.eq(BODY), Mockito.eq(CATEGORY_ID),Mockito.eq(USER_ID), Mockito.eq(null), Mockito.eq(MOVIES)))
////                .thenReturn(2L);
////
//////        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
////        final Long id = postService.register(TITLE,  BODY, CATEGORY_ID, USER_ID,null, MOVIES);
////
//////        3. Validaciones: Confirmo las postcondiciones
////        Assert.assertEquals(new Long(2), id);
////    }
//
//    @Test
//    public void testFindPostById() {
////        1. Setup: Establezco las pre-condiociones
//        Optional<Post> optional = Optional.empty();
//        Mockito.when(dao.findPostById(Mockito.eq(1), Mockito.eq(EnumSet.noneOf(PostDao.FetchRelation.class))))
//                .thenReturn(optional);
//
////        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
//        final Optional<Post> postById = postService.findPostById(1);
//
////        3. Validaciones: Confirmo las postcondiciones
//        Assert.assertFalse(postById.isPresent());
//    }
//
//    @Test
//    public void testFindPostsByMovieId() {
////        1. Setup: Establezco las pre-condiociones
//        final ArrayList<Post> posts = new ArrayList<Post>(){{
//            add(Mockito.mock(Post.class));
//            add(Mockito.mock(Post.class));
//            add(Mockito.mock(Post.class));
//        }};
//
//        Mockito.when(dao.findPostsByMovieId(Mockito.eq(1L), Mockito.eq(EnumSet.noneOf(PostDao.FetchRelation.class))))
//                .thenReturn(posts);
//
////        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
//        final Collection<Post> postsByMovieId = postService.findPostsByMovieId(1L);
//
////        3. Validaciones: Confirmo las postcondiciones
//        Assert.assertNotNull(postsByMovieId);
//        Assert.assertEquals(3, postsByMovieId.size());
//    }
//
//    @Test
//    public void testGetAllPostCategories() {
////        1. Setup: Establezco las pre-condiociones
//        final ArrayList<PostCategory> postCategories = new ArrayList<PostCategory>(){{
//            add(Mockito.mock(PostCategory.class));
//            add(Mockito.mock(PostCategory.class));
//            add(Mockito.mock(PostCategory.class));
//        }};
//
//        Mockito.when(categoryDao.getAllPostCategories()).thenReturn(postCategories);
//
////        2. Ejercito la class under test -> ÚNICA INVOCACIÓN
//        final Collection<PostCategory> allPostCategories = postService.getAllPostCategories();
//
////        3. Validaciones: Confirmo las postcondiciones
//        Assert.assertNotNull(allPostCategories);
//        Assert.assertEquals(3, allPostCategories.size());
//    }
//}
