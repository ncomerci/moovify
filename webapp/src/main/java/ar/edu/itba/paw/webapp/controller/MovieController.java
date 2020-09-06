package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.MovieService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.webapp.exceptions.MovieNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;

@Controller
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private PostService postService;

    @RequestMapping( path = "/movie/create",  method = RequestMethod.GET)
    public ModelAndView create(){
        return new ModelAndView("movie/create");
    }

    @RequestMapping( path = "/movie/register", method = RequestMethod.POST)
    public ModelAndView register(@RequestParam final String title, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") final LocalDate premierDate){
        final Movie movie = movieService.register(title, premierDate);
        return new ModelAndView("redirect:/movie/" + movie.getId());
    }

    @RequestMapping(path = "/movie/{id}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable final long id) {

        final ModelAndView mv = new ModelAndView("movie/view");
        mv.addObject("movie", movieService.findById(id).orElseThrow(MovieNotFoundException::new));
        mv.addObject("posts", postService.findPostsByMovieId(id, false));
        return mv;
    }
}
