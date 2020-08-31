package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @RequestMapping(path = "/post/create", method = RequestMethod.GET)
    public ModelAndView create() {

        final ModelAndView mv = new ModelAndView("post/create");

        return mv;
    }

    @RequestMapping(path = "/post/create", method = RequestMethod.POST)
    public ModelAndView submit() {

        final ModelAndView mv = new ModelAndView("redirect:/post/view");

        return mv;
    }

    // TODO: Parametrize post ID
    @RequestMapping(path = "/post/view", method = RequestMethod.GET)
    public ModelAndView view() {

        final ModelAndView mv = new ModelAndView("post/view");
        mv.addObject("post", postService.findById(1));

        return mv;
    }
}
