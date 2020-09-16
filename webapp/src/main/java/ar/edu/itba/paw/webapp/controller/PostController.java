package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private CommentService commentService;


    @RequestMapping(path = "/post/{postId}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable final long postId) {

        final ModelAndView mv = new ModelAndView("post/view");
        mv.addObject("post", postService.findPostById(postId)
                .orElseThrow(PostNotFoundException::new));

        return mv;
    }

    @RequestMapping(path = "/post/create", method = RequestMethod.GET)
    public ModelAndView write() {

        final ModelAndView mv = new ModelAndView("post/create");
        mv.addObject("movies", movieService.getAllMovies());

        return mv;
    }
    // TODO unificar tags y movies, uno es collection y el otro es set
    @RequestMapping(path = "/post/create" , method = RequestMethod.POST)
    public ModelAndView create(@RequestParam final String title, @RequestParam final String email,
                               @RequestParam final String body, @RequestParam(value = "tags[]" , required = false) Collection<String> tags, @RequestParam(value = "movies[]", required = false) Set<Long> movies){

        // movies default value is an empty Set. (Overrides Spring default value of null)
        if(movies == null)
            movies = Collections.emptySet();

        if(tags == null)
            tags = Collections.emptySet();

        final Post post = postService.register(title, email, body, tags, movies);
        return new ModelAndView("redirect:/post/" + post.getId());

    }
}
