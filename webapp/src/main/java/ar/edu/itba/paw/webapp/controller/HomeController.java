package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.PostService;

@Controller
public class HomeController {

    @Autowired
    private PostService postService;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ModelAndView helloWorld() {

        final ModelAndView mv = new ModelAndView("index");
        mv.addObject("posts", postService.getAllPosts(false));

        return mv;
    }

}