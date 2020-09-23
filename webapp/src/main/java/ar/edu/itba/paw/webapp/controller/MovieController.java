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
import java.util.Collection;

@Controller
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private PostService postService;

    @RequestMapping(path = "/movie/create",  method = RequestMethod.GET)
    public ModelAndView create(){
        ModelAndView mv = new ModelAndView("movie/create");
        mv.addObject("categories", movieService.getAvailableCategories());
        return mv;
    }

    @RequestMapping(path = "/movie/register", method = RequestMethod.POST)
    public ModelAndView register(
            @RequestParam final String title,
            @RequestParam final String originalTitle,
            @RequestParam final long tmdbId,
            @RequestParam final String imdbId,
            @RequestParam final String originalLanguage,
            @RequestParam final String overview,
            @RequestParam final float popularity,
            @RequestParam final float runtime,
            @RequestParam final float voteAverage,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") final LocalDate releaseDate,
            @RequestParam(name = "categories") Collection<Long> categories){

        final Movie movie = movieService.register(title, originalTitle,  tmdbId,  imdbId,  originalLanguage,
                overview,  popularity,  runtime,  voteAverage,  releaseDate,  categories);
        return new ModelAndView("redirect:/movie/" + movie.getId());
    }

    @RequestMapping(path = "/movie/{id}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable final long id) {

        final ModelAndView mv = new ModelAndView("movie/view");
        mv.addObject("movie", movieService.findById(id).orElseThrow(MovieNotFoundException::new));
        mv.addObject("posts", postService.findPostsByMovieId(id));
        return mv;
    }
}
