package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.webapp.exceptions.CommentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CommentController {
    
    @Autowired
    private CommentService commentService;

    @RequestMapping( path = "/comment/create",  method = RequestMethod.GET)
    public ModelAndView create(){
        return new ModelAndView("comment/create");
    }

    @RequestMapping( path = "/comment/create",  method = RequestMethod.POST)
    public ModelAndView post(@RequestParam final long postId, @RequestParam(value = "parentId", required = false) final Long parentId,
                             @RequestParam final String userEmail, @RequestParam final String body){
        commentService.register(postId, parentId, body, userEmail);
        return new ModelAndView("comment/create");
    }

    @RequestMapping(path = "/comment/{id}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable final long id) {

        final ModelAndView mv = new ModelAndView("comment/view");
        mv.addObject("comment", commentService.findCommentById(id).orElseThrow(CommentNotFoundException::new));
        return mv;
    }
}
