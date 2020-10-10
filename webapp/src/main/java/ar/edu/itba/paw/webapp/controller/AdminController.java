package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.CommentNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.InvalidSearchArgumentsException;
import ar.edu.itba.paw.webapp.exceptions.PostNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private SearchService searchService;

    @RequestMapping(path = "/admin/deleted/posts", method = RequestMethod.GET)
    public ModelAndView deletedPosts(@RequestParam(defaultValue = "") final String query,
                                     @RequestParam(defaultValue = "5") final int pageSize,
                                     @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /admin/deleted/posts with query = {}; pageSize = {} and pageNumber = {}", query, pageSize, pageNumber);

        final ModelAndView mv = new ModelAndView("adminPanel/deleted/posts");

        mv.addObject("query", query);
        mv.addObject("posts", searchService.searchDeletedPosts(query, pageNumber, pageSize)
                .orElseThrow(InvalidSearchArgumentsException::new));

        return mv;
    }

    @RequestMapping(path = "/admin/deleted/comments", method = RequestMethod.GET)
    public ModelAndView deletedComments(@RequestParam(defaultValue = "") final String query,
                                        @RequestParam(defaultValue = "5") final int pageSize,
                                        @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /admin/deleted/comments with query = {}; pageSize = {} and pageNumber = {}", query, pageSize, pageNumber);

        final ModelAndView mv = new ModelAndView("adminPanel/deleted/comments");

        mv.addObject("query", query);
        mv.addObject("comments", searchService.searchDeletedComments(query, pageNumber, pageSize)
                .orElseThrow(InvalidSearchArgumentsException::new));

        return mv;
    }

    @RequestMapping(path = "/admin/deleted/users", method = RequestMethod.GET)
    public ModelAndView deletedUsers(@RequestParam(defaultValue = "") final String query,
                                     @RequestParam(defaultValue = "5") final int pageSize,
                                     @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /admin/deleted/users with query = {}; pageSize = {} and pageNumber = {}", query, pageSize, pageNumber);

        final ModelAndView mv = new ModelAndView("adminPanel/deleted/users");

        mv.addObject("query", query);
        mv.addObject("users", searchService.searchDeletedUsers(query, pageNumber, pageSize)
                .orElseThrow(InvalidSearchArgumentsException::new));

        return mv;
    }

    //    ================ COMMENTS PRIVILEGES ================

    @RequestMapping(path = "/comment/delete/{commentId}", method = RequestMethod.POST)
    public ModelAndView deleteComment(@PathVariable final long commentId) {

        LOGGER.info("Accessed /comment/delete/{} to delete comment. Redirecting to /comment/{}", commentId, commentId);

        final Comment comment = commentService.findCommentById(commentId).orElseThrow(CommentNotFoundException::new);

        commentService.deleteComment(comment);

        return new ModelAndView("redirect:/comment/" + comment.getId());
    }

    @RequestMapping(path = "/comment/restore/{commentId}", method = RequestMethod.POST)
    public ModelAndView restoreComment(@PathVariable final long commentId) {

        LOGGER.info("Accessed /comment/restore/{} to restore comment. Redirecting to /comment/{}", commentId, commentId);

        final Comment comment = commentService.findCommentById(commentId).orElseThrow(CommentNotFoundException::new);

        commentService.restoreComment(comment);

        return new ModelAndView("redirect:/comment/" + comment.getId());
    }

    //    ================ POSTS PRIVILEGES ================

    @RequestMapping(path = "/post/delete/{postId}", method = RequestMethod.POST)
    public ModelAndView deletePost(@PathVariable final long postId) {

        LOGGER.info("Accessed /post/delete/{} to delete post. Redirecting to /", postId);

        final Post post = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);

        postService.deletePost(post);

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path = "/post/restore/{postId}", method = RequestMethod.POST)
    public ModelAndView restorePost(@PathVariable final long postId) {

        LOGGER.info("Accessed /post/restore/{} to restore post. Redirecting to /post/{}", postId, postId);

        final Post post = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);

        postService.restorePost(post);

        return new ModelAndView("redirect:/post/" + post.getId());
    }

    //    ================ USERS PRIVILEGES ================

    @RequestMapping(path = "/user/promote/{userId}", method = RequestMethod.POST)
    public ModelAndView promoteUser(@PathVariable long userId, RedirectAttributes redirectAttributes) {

        LOGGER.info("Accessed /user/promote/{} to promote user to admin. Redirecting to /user/{}", userId, userId);

        final User user = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        userService.promoteUserToAdmin(user);

        redirectAttributes.addFlashAttribute("user", user);

        return new ModelAndView("redirect:/user/" + user.getId());
    }

    @RequestMapping(path = "/user/delete/{userId}", method = RequestMethod.POST)
    public ModelAndView deleteUser(@PathVariable long userId) {

        LOGGER.info("Accessed /user/delete/{} to delete user. Redirecting to /", userId);

        final User user = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        userService.deleteUser(user);

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path = "/user/restore/{userId}", method = RequestMethod.POST)
    public ModelAndView restoreUser(@PathVariable long userId) {

        LOGGER.info("Accessed /user/restore/{} to restore user. Redirecting to /user/{}", userId, userId);

        final User user = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        userService.restoreUser(user);

        return new ModelAndView("redirect:/user/" + user.getId());
    }
}
