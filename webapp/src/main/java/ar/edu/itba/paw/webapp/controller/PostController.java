package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.webapp.exceptions.PostNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;
import java.util.List;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @RequestMapping(path = "/post/create", method = RequestMethod.GET)
    public ModelAndView write() {

        return new ModelAndView("post/create");
    }

    @RequestMapping(path = "/post/create" , method = RequestMethod.POST)
    public ModelAndView create(@RequestParam("title") final String title, @RequestParam("email") final String email,
                               @RequestParam("body") final String body, @RequestParam("movies") List<Long> movies){

        final Post post = postService.register(title, email, body, new HashSet<>(movies));
        return new ModelAndView("redirect:/post/" + post.getId());

    }

    @RequestMapping(path = "/post/{id}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable("id") final long id) {

        final ModelAndView mv = new ModelAndView("post/view");
        mv.addObject("post", postService.findById(id).orElseThrow(PostNotFoundException::new));
        return mv;
    }
}
