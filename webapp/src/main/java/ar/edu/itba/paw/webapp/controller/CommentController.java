package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.CommentNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.CommentCreateForm;
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
    
    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/comment/create",  method = RequestMethod.POST)
    public ModelAndView post(@Valid @ModelAttribute("CommentCreateForm") final CommentCreateForm commentCreateForm,
                             final BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes,
                             final HttpServletRequest request) {

        redirectAttributes.addFlashAttribute(commentCreateForm);

        if(!bindingResult.hasErrors()){
            User user = userService.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

            commentService.register(commentCreateForm.getPostId(), commentCreateForm.getParentId(),
                    commentCreateForm.getCommentBody(), user.getId());
        }

//        Goes back to the specific view that generated the request
        return new ModelAndView("redirect:"+ request.getHeader("Referer") + "#comment-section");
    }

    @RequestMapping(path = "/comment/like",  method = RequestMethod.POST)
    public ModelAndView post(@RequestParam final long post_id, @RequestParam final long comment_id,
                             @RequestParam(defaultValue = "0") final int value,
                             final Principal principal,
                             final HttpServletRequest request) {

        User user = userService.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
        Comment comment = commentService.findCommentById(comment_id).orElseThrow(CommentNotFoundException::new);
        commentService.likeComment(comment, user, value);

        return new ModelAndView("redirect:" + request.getHeader("Referer") + "#comment-section");

    }

    @RequestMapping(path = "/comment/{id}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable final long id,
                             @RequestParam(defaultValue = "5") final int pageSize,
                             @RequestParam(defaultValue = "0") final int pageNumber,
                             @ModelAttribute("CommentCreateForm") final CommentCreateForm commentCreateForm) {

        final ModelAndView mv = new ModelAndView("comment/view");

        mv.addObject("comment", commentService.findCommentById(id).orElseThrow(CommentNotFoundException::new));
        mv.addObject("children", commentService.findCommentDescendants(id, pageNumber, pageSize));

        return mv;
    }
}
