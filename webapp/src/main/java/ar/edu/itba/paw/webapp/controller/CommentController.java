package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.CommentNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.PostNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.CommentCreateForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@Controller
public class CommentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);
    
    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/comment/create",  method = RequestMethod.POST)
    public ModelAndView post(@Valid @ModelAttribute("CommentCreateForm") final CommentCreateForm commentCreateForm,
                             final BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes,
                             final HttpServletRequest request) {

        redirectAttributes.addFlashAttribute(commentCreateForm);

        if(!bindingResult.hasErrors()){

            final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
            final Post post = postService.findPostById(commentCreateForm.getPostId()).orElseThrow(PostNotFoundException::new);
            final Comment parent = commentService.findCommentById(commentCreateForm.getParentId()).orElseThrow(CommentNotFoundException::new);

            commentService.register(post, parent,
                    commentCreateForm.getCommentBody(), user, "newCommentEmail");

            LOGGER.info("Accessed /comment/create and Created Comment successfully. Redirecting to {}{}", request.getHeader("Referer"), "#comment-section");
        }
        else
            LOGGER.warn("Accessed /comment/create and errors were found in creation form. Redirecting to {}{}", request.getHeader("Referer"), "#comment-section");

//        Goes back to the specific view that generated the request
        return new ModelAndView("redirect:"+ request.getHeader("Referer") + "#comment-section");
    }

    @RequestMapping(path = "/comment/{id}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable final long id,
                             @RequestParam(defaultValue = "5") final int pageSize,
                             @RequestParam(defaultValue = "0") final int pageNumber,
                             @ModelAttribute("CommentCreateForm") final CommentCreateForm commentCreateForm) {

        LOGGER.info("Accessed /comment/{}", id);

        final ModelAndView mv = new ModelAndView("comment/view");

        final Comment comment = commentService.findCommentById(id).orElseThrow(CommentNotFoundException::new);

        mv.addObject("comment", comment);
        mv.addObject("children", commentService.findCommentDescendants(comment, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = "/comment/like",  method = RequestMethod.POST)
    public ModelAndView post(@RequestParam final long comment_id,
                             @RequestParam(defaultValue = "0") final int value,
                             final Principal principal,
                             final HttpServletRequest request) {

        LOGGER.info("Accessed /comment/like");

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
        final Comment comment = commentService.findCommentById(comment_id).orElseThrow(CommentNotFoundException::new);

        commentService.likeComment(comment, user, value);

        return new ModelAndView("redirect:" + request.getHeader("Referer") + "#comment-section");

    }
}
