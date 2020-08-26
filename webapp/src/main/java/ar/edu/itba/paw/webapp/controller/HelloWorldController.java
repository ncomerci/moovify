package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.UserService;

@Controller
public class HelloWorldController {

    @Autowired
    private UserService us;

    @RequestMapping("/")
    public ModelAndView helloWorld() {

        final ModelAndView mv = new ModelAndView("index");
        mv.addObject("user", us.findById(1));

        return mv;
    }

}