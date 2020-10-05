package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    private static final int HOME_PAGE_POST_COUNT = 10;
    @Autowired
    private PostService postService;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ModelAndView helloWorld() {

        final ModelAndView mv = new ModelAndView("index");

        mv.addObject("newestPosts", postService.getAllPostsOrderByNewest(0, HOME_PAGE_POST_COUNT));
        mv.addObject("hottestPosts", postService.getAllPostsOrderByHottest(0, HOME_PAGE_POST_COUNT));

        return mv;
    }

}