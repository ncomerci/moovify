package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.MovieService;
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
import java.util.Set;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private MovieService movieService;

    @RequestMapping(path = "/post/create", method = RequestMethod.GET)
    public ModelAndView write() {

        final ModelAndView mv = new ModelAndView("post/create");
        mv.addObject("movies", movieService.getAllMovies());

        return mv;
    }

    @RequestMapping(path = "/post/create" , method = RequestMethod.POST)
    public ModelAndView create(@RequestParam final String title, @RequestParam final String email,
                               @RequestParam final String body, @RequestParam(value = "movies[]", required = false) Set<Long> movies){

        // movies default value is an empty Set. (Overrides Spring default value of null)
        if(movies == null)
            movies = new HashSet<>();

        final Post post = postService.register(title, email, body, movies);
        return new ModelAndView("redirect:/post/" + post.getId());

    }

    @RequestMapping(path = "/post/{id}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable final long id) {

        final ModelAndView mv = new ModelAndView("post/view");
        mv.addObject("post", postService.findById(id).orElseThrow(PostNotFoundException::new));

        return mv;
    }
}
