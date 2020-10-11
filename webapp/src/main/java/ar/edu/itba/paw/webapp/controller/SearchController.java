package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.webapp.exceptions.InvalidSearchArgumentsException;
import ar.edu.itba.paw.webapp.form.SearchMoviesForm;
import ar.edu.itba.paw.webapp.form.SearchPostsForm;
import ar.edu.itba.paw.webapp.form.SearchUsersForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class SearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    @RequestMapping(path = "/search/posts", method = RequestMethod.GET)
    public ModelAndView searchPosts(@ModelAttribute("searchPostsForm") final SearchPostsForm searchPostsForm,
                                    @RequestParam(defaultValue = "5") final int pageSize,
                                    @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /search/posts");

        final ModelAndView mv = new ModelAndView("search/posts");

        mv.addObject("query", searchPostsForm.getQuery());
        mv.addObject("categories", searchService.getPostCategories());
        mv.addObject("periodOptions", searchService.getPostPeriodOptions());
        mv.addObject("sortCriteria", searchService.getAllPostSortCriteria());

        mv.addObject("posts", searchService.searchPosts(searchPostsForm.getQuery(),
                searchPostsForm.getPostCategory(), searchPostsForm.getPostAge(), searchPostsForm.getSortCriteria(),
                pageNumber, pageSize).orElseThrow(InvalidSearchArgumentsException::new));

        return mv;
    }

    @RequestMapping(path = "/search/movies", method = RequestMethod.GET)
    public ModelAndView searchMovies(@ModelAttribute("searchMoviesForm") final SearchMoviesForm searchMoviesForm,
                                     @RequestParam(defaultValue = "5") final int pageSize,
                                     @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /search/movies");

        final ModelAndView mv = new ModelAndView("search/movies");

        mv.addObject("query", searchMoviesForm.getQuery());
        mv.addObject("categories", searchService.getMoviesCategories());
        mv.addObject("decades", searchService.getMoviesDecades());
        mv.addObject("sortCriteria", searchService.getAllMoviesSortCriteria());

        mv.addObject("movies",
                searchService.searchMovies(searchMoviesForm.getQuery(), searchMoviesForm.getMovieCategory(),
                        searchMoviesForm.getDecade(), searchMoviesForm.getSortCriteria(),
                        pageNumber, pageSize).orElseThrow(InvalidSearchArgumentsException::new));
        return mv;
    }

    @RequestMapping(path = "/search/users", method = RequestMethod.GET)
    public ModelAndView searchUsers(@ModelAttribute("searchUsersForm") final SearchUsersForm searchUsersForm,
                                    @RequestParam(defaultValue = "5") final int pageSize,
                                    @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /search/users");

        final ModelAndView mv = new ModelAndView("search/users");

        mv.addObject("query", searchUsersForm.getQuery());
        mv.addObject("roleOptions", searchService.getUserRoleOptions());
        mv.addObject("sortCriteria", searchService.getAllUserSortCriteria());

        mv.addObject("users",
                searchService.searchUsers(searchUsersForm.getQuery(),searchUsersForm.getRole(),
                        searchUsersForm.getSortCriteria(), pageNumber, pageSize).orElseThrow(InvalidSearchArgumentsException::new));

        return mv;
    }
}

