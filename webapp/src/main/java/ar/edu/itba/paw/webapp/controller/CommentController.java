package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.CommentNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class CommentController {
    
    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/comment/create",  method = RequestMethod.GET)
    public ModelAndView create(){
        return new ModelAndView("comment/create");
    }

    @RequestMapping(path = "/comment/create",  method = RequestMethod.POST)
    public ModelAndView post(@RequestParam final long postId, @RequestParam(required = false) Long parentId,
                             @RequestParam final String body, Principal principal){

        User user = userService.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        commentService.register(postId, parentId, body, user.getId());
        return new ModelAndView("comment/create");
    }

    @RequestMapping(path = "/comment/{id}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable final long id) {

        final ModelAndView mv = new ModelAndView("comment/view");
        mv.addObject("comment", commentService.findCommentByIdWithoutChildren(id).orElseThrow(CommentNotFoundException::new));
        return mv;
    }
}
