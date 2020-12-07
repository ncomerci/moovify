//package ar.edu.itba.paw.webapp.controller;
//
//import ar.edu.itba.paw.interfaces.services.CommentService;
//import ar.edu.itba.paw.interfaces.services.MovieService;
//import ar.edu.itba.paw.interfaces.services.PostService;
//import ar.edu.itba.paw.interfaces.services.UserService;
//import ar.edu.itba.paw.interfaces.services.exceptions.IllegalPostEditionException;
//import ar.edu.itba.paw.interfaces.services.exceptions.IllegalPostLikeException;
//import ar.edu.itba.paw.interfaces.services.exceptions.MissingPostEditPermissionException;
//import ar.edu.itba.paw.models.Post;
//import ar.edu.itba.paw.models.PostCategory;
//import ar.edu.itba.paw.models.User;
//import ar.edu.itba.paw.webapp.exceptions.InvalidPostCategoryException;
//import ar.edu.itba.paw.webapp.exceptions.PostNotFoundException;
//import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
//import ar.edu.itba.paw.webapp.form.CommentCreateForm;
//import ar.edu.itba.paw.webapp.form.PostCreateForm;
//import ar.edu.itba.paw.webapp.form.PostEditForm;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.validation.Valid;
//import java.security.Principal;
//import java.util.Collections;
//
//
//@Controller
//public class PostController {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);
//
//    @Autowired
//    private PostService postService;
//
//    @Autowired
//    private MovieService movieService;
//
//    @Autowired
//    private CommentService commentService;
//
//    @Autowired
//    private UserService userService;
//
//
//    @RequestMapping(path = "/post/{postId}", method = RequestMethod.GET)
//    public ModelAndView view(@PathVariable final long postId,
//                             @RequestParam(defaultValue = "10") final int pageSize,
//                             @RequestParam(defaultValue = "0") final int pageNumber,
//                             @ModelAttribute("commentCreateForm") final CommentCreateForm commentCreateForm) {
//
//        LOGGER.info("Accessed /post/{}", postId);
//
//        final ModelAndView mv = new ModelAndView("post/view");
//
//        final Post post = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);
//
//        mv.addObject("post", post);
//        mv.addObject("comments", commentService.findPostCommentDescendants(post, pageNumber, pageSize));
//        mv.addObject("maxDepth", commentService.getMaxCommentTreeDepth());
//
//        return mv;
//    }
//
//    @RequestMapping(path = "/post/like", method = RequestMethod.POST)
//    public ModelAndView likePost(@RequestParam final long postId,
//                                 @RequestParam(defaultValue = "0") final int value,
//                                 final Principal principal) throws IllegalPostLikeException {
//
//        LOGGER.info("Accessed /post/like");
//
//        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
//
//        final Post post = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);
//
//        postService.likePost(post, user, value);
//
//        return new ModelAndView("redirect:/post/" + postId);
//    }
//
//    @RequestMapping(path = "/post/create", method = RequestMethod.GET)
//    public ModelAndView showPostCreateForm(@ModelAttribute("postCreateForm") final PostCreateForm postCreateForm) {
//
//        LOGGER.info("Accessed /post/create");
//
//        final ModelAndView mv = new ModelAndView("post/create");
//
//        mv.addObject("movies", movieService.getAllMoviesNotPaginated());
//        mv.addObject("categories", postService.getAllPostCategories());
//
//        return mv;
//    }
//
//    @RequestMapping(path = "/post/create", method = RequestMethod.POST)
//    public ModelAndView showPostCreateForm(@Valid @ModelAttribute("postCreateForm") final PostCreateForm postCreateForm,
//                                           final BindingResult errors,
//                                           final Principal principal) {
//
//        if (errors.hasErrors()) {
//            LOGGER.warn("Errors were found in the form postCreateForm creating a Post");
//            return showPostCreateForm(postCreateForm);
//        }
//
//        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
//
//        final PostCategory postCategory = postService.findCategoryById(postCreateForm.getCategory())
//                .orElseThrow(InvalidPostCategoryException::new);
//
//        final Post post = postService.register(postCreateForm.getTitle(), postCreateForm.getBody(),
//                    postCategory, user,
//                    postCreateForm.getTags() == null ? Collections.emptySet() : postCreateForm.getTags(),
//                    postCreateForm.getMovies());
//
//        LOGGER.info("Accessed /post/create to create Post. Redirecting to /post/{}", post.getId());
//
//        return new ModelAndView("redirect:/post/" + post.getId());
//    }
//
//    @RequestMapping(path = "/post/edit/{postId}", method = RequestMethod.GET)
//    public ModelAndView showPostEditForm(@PathVariable long postId, @ModelAttribute("postEditForm") final PostEditForm postEditForm,
//                                         Principal principal) throws MissingPostEditPermissionException, IllegalPostEditionException {
//
//        LOGGER.info("Accessed /post/edit/{}", postId);
//
//        final ModelAndView mv = new ModelAndView("post/edit");
//
//        final Post post = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);
//
//        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
//
//        postService.guaranteePostEditionPermissions(user, post);
//
//        mv.addObject("post", post);
//
//        if(postEditForm.getBody() == null)
//            postEditForm.setBody(post.getBody());
//
//        return mv;
//    }
//
//    @RequestMapping(path = "/post/edit/{postId}", method = RequestMethod.POST)
//    public ModelAndView editPost(@PathVariable long postId, @Valid @ModelAttribute("postEditForm") final PostEditForm postEditForm,
//                                           final BindingResult errors, final Principal principal) throws MissingPostEditPermissionException, IllegalPostEditionException {
//
//        if(errors.hasErrors()) {
//            LOGGER.warn("Errors were found in the form postEditForm editing a Post");
//            return showPostEditForm(postId, postEditForm, principal);
//        }
//
//        final Post post = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);
//
//        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
//
//        postService.editPost(user, post, postEditForm.getBody());
//
//        LOGGER.info("Accessed /post/edit to edit Post. Redirecting to /post/{}", post.getId());
//
//        return new ModelAndView("redirect:/post/" + post.getId());
//    }
//}
