package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.UserService;

@Controller
public class HelloWorldController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public ModelAndView helloWorld() {

        final ModelAndView mv = new ModelAndView("index");
        mv.addObject("user", userService.findById(1));

        return mv;
    }

    @RequestMapping("/post/create")
    public ModelAndView createPost() {

        final ModelAndView mv = new ModelAndView("post/create");
        mv.addObject("user", userService.findById(1));

        return mv;
    }

}