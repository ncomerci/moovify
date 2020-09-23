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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

        User user = userService.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
        redirectAttributes.addFlashAttribute(commentCreateForm);

        if(!bindingResult.hasErrors()){
            commentService.register(commentCreateForm.getPostId(), commentCreateForm.getParentId(),
                    commentCreateForm.getCommentBody().replaceAll("\\s+", " ").replaceAll("^ | $", ""), user.getId());
        }
        return new ModelAndView("redirect:/post/" + commentCreateForm.getPostId());
    }

    @RequestMapping(path = "/comment/{id}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable final long id) {

        final ModelAndView mv = new ModelAndView("comment/view");
        mv.addObject("comment", commentService.findCommentByIdWithoutChildren(id).orElseThrow(CommentNotFoundException::new));
        return mv;
    }
}
