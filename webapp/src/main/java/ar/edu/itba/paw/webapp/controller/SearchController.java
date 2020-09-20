package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.webapp.exceptions.NonExistingSearchCriteriaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;


@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @RequestMapping(path = "/search/posts/", method = RequestMethod.GET)
    public ModelAndView searchPosts(@RequestParam() final String query,
                                    @RequestParam(value = "sort-criteria", defaultValue = "newest") final String sortCriteria,
                                    @RequestParam(value = "post-category", defaultValue = "all") final String postCategory,
                                    @RequestParam(value = "post-age", defaultValue = "all-time") final String postAge) {

        final ModelAndView mv = new ModelAndView("search/posts");
        mv.addObject("query", query);
        mv.addObject("posts", searchService.searchPosts(query, postCategory, postAge, sortCriteria));
        return mv;
    }

    @RequestMapping(path = "/search/movies/", method = RequestMethod.GET)
    public ModelAndView searchMovies(@RequestParam() final String query) {

        final ModelAndView mv = new ModelAndView("search/movies");
        //TODO la vista esta vacia, no hay metodo que permita conseguir las peliculas
        // y tampoco hay un rendereado listo en la vista
        mv.addObject("query", query);
        return mv;
    }

    @RequestMapping(path = "/search/users/", method = RequestMethod.GET)
    public ModelAndView searchUsers(@RequestParam() final String query) {

        //TODO la vista esta vacia, no hay metodo que permita conseguir los usuarios
        // y tampoco hay un rendereado listo en la vista
        final ModelAndView mv = new ModelAndView("search/users");
        mv.addObject("query", query);
        return mv;
    }

}

