package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.UserService;
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
                             final BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute(commentCreateForm);

        if(!bindingResult.hasErrors()){
            User user = userService.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

            commentService.register(commentCreateForm.getPostId(), commentCreateForm.getParentId(),
                    commentCreateForm.getCommentBody(), user.getId());
        }

        return new ModelAndView("redirect:/post/" + commentCreateForm.getPostId());
    }

    @RequestMapping(path = "/comment/{id}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable final long id,
                             @RequestParam(defaultValue = "5") final int pageSize,
                             @RequestParam(defaultValue = "0") final int pageNumber) {

        final ModelAndView mv = new ModelAndView("comment/view");
        mv.addObject("comment", commentService.findCommentByIdWithChildren(id,pageNumber,pageSize).orElseThrow(CommentNotFoundException::new));
        return mv;
    }
}
