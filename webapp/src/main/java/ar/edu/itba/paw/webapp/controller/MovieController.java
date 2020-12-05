//package ar.edu.itba.paw.webapp.controller;
//
//import ar.edu.itba.paw.interfaces.services.MovieService;
//import ar.edu.itba.paw.interfaces.services.PostService;
//import ar.edu.itba.paw.models.Movie;
//import ar.edu.itba.paw.webapp.exceptions.MovieNotFoundException;
//import ar.edu.itba.paw.webapp.exceptions.MoviePosterNotFoundException;
//import ar.edu.itba.paw.webapp.dto.inputDto.UpdateMoviePosterForm;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Controller;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.Collection;
//
//@Controller
//public class MovieController {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(MovieController.class);
//
//    @Autowired
//    private MovieService movieService;
//
//    @Autowired
//    private PostService postService;
//
//    @RequestMapping(path = "/movie/create",  method = RequestMethod.GET)
//    public ModelAndView create(){
//
//        LOGGER.info("Accessed /movie/create");
//
//        final ModelAndView mv = new ModelAndView("movie/create");
//
//        mv.addObject("categories", movieService.getAvailableCategories());
//
//        return mv;
//    }
//
//    @RequestMapping(path = "/movie/create", method = RequestMethod.POST)
//    public ModelAndView register(
//            @RequestParam final String title,
//            @RequestParam final String originalTitle,
//            @RequestParam final long tmdbId,
//            @RequestParam final String imdbId,
//            @RequestParam final String originalLanguage,
//            @RequestParam final String overview,
//            @RequestParam final float popularity,
//            @RequestParam final float runtime,
//            @RequestParam final float voteAverage,
//            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") final LocalDate releaseDate,
//            @RequestParam(name = "categories") Collection<Long> categories){
//
//        final Movie movie = movieService.register(title, originalTitle,  tmdbId,  imdbId,  originalLanguage,
//                overview,  popularity,  runtime,  voteAverage,  releaseDate,  categories);
//
//        LOGGER.info("Accessed /movie/create to create Movie. Redirecting to /movie/{}", movie.getId());
//
//        return new ModelAndView("redirect:/movie/" + movie.getId());
//    }
//
//    @RequestMapping(path = "/movie/{movieId}", method = RequestMethod.GET)
//    public ModelAndView view(@PathVariable final long movieId,
//                             @RequestParam(defaultValue = "10") final int pageSize,
//                             @RequestParam(defaultValue = "0") final int pageNumber) {
//
//        LOGGER.info("Accessed /movie/{}", movieId);
//
//        final ModelAndView mv = new ModelAndView("movie/view");
//
//        final Movie movie = movieService.findMovieById(movieId).orElseThrow(MovieNotFoundException::new);
//
//        mv.addObject("movie", movie);
//        mv.addObject("posts", postService.findPostsByMovie(movie, pageNumber, pageSize));
//
//        return mv;
//    }
//
//    @RequestMapping(path = "/movie/{movieId}/poster/update", method = RequestMethod.GET)
//    public ModelAndView showUpdatePoster(@PathVariable final long movieId,
//                                         @ModelAttribute("updateMoviePosterForm") UpdateMoviePosterForm updateMoviePosterForm) {
//
//        LOGGER.info("Accessed /movie/{}/poster/update.", movieId);
//
//        final ModelAndView mv = new ModelAndView("movie/updatePoster");
//
//        mv.addObject(movieService.findMovieById(movieId).orElseThrow(MovieNotFoundException::new));
//
//        return mv;
//    }
//
//    @RequestMapping(path = "/movie/{movieId}/poster/update", method = RequestMethod.POST)
//    public ModelAndView updatePoster(@PathVariable final long movieId,
//                                     @ModelAttribute("updateMoviePosterForm") UpdateMoviePosterForm updateMoviePosterForm,
//                                     final BindingResult bindingResult) throws IOException {
//
//        if(bindingResult.hasErrors()) {
//            LOGGER.warn("Errors were found in the form updateMoviePosterForm updating movie poster in /movie/{}/poster/update", movieId);
//            return showUpdatePoster(movieId, updateMoviePosterForm);
//        }
//
//        final Movie movie = movieService.findMovieById(movieId).orElseThrow(MovieNotFoundException::new);
//
//        movieService.updatePoster(movie, updateMoviePosterForm.getPoster().getBytes());
//
//        return new ModelAndView("redirect:/movie/" + movie.getId());
//    }
//
//    @RequestMapping(path = "/movie/poster/{posterId}", method = RequestMethod.GET, produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
//    public @ResponseBody
//    byte[] getPoster(@PathVariable long posterId) {
//
//        LOGGER.info("Accessed /movie/poster/{}", posterId);
//        return movieService.getPoster(posterId).orElseThrow(MoviePosterNotFoundException::new);
//    }
//}
