package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.MovieService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.webapp.exceptions.PostNotFoundException;
import ar.edu.itba.paw.webapp.form.PostCreateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private MovieService movieService;


    @RequestMapping(path = "/post/{postId}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable final long postId) {

        final ModelAndView mv = new ModelAndView("post/view");
        mv.addObject("post", postService.findPostById(postId)
                .orElseThrow(PostNotFoundException::new));

        return mv;
    }


    @RequestMapping(path = "/post/create", method = RequestMethod.GET )
    public ModelAndView showPostCreateForm(@ModelAttribute("postCreateForm") final PostCreateForm postCreateForm) {
        final ModelAndView mv = new ModelAndView("post/create");
        mv.addObject("movies", movieService.getAllMovies());
        mv.addObject("categories", postService.getAllPostCategories());

        return mv;
    }

    @RequestMapping(path = "/post/create", method = RequestMethod.POST )
    public ModelAndView showPostCreateForm(@Valid @ModelAttribute("postCreateForm") final PostCreateForm postCreateForm, final BindingResult errors) {
        if (errors.hasErrors()) {
            return showPostCreateForm(postCreateForm);
        }

        final Post post = postService.register(postCreateForm.getTitle(), postCreateForm.getEmail(), postCreateForm.getBody(), postCreateForm.getTags() == null ? Collections.emptySet():  postCreateForm.getTags() , postCreateForm.getMovies());
        return new ModelAndView("redirect:/post/" + post.getId());
    }
}
