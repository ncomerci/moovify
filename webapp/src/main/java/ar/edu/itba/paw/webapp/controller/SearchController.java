package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.webapp.form.SearchMoviesForm;
import ar.edu.itba.paw.webapp.form.SearchPostsForm;
import ar.edu.itba.paw.webapp.form.SearchUsersForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @RequestMapping(path = "/search/posts/", method = RequestMethod.GET)
    public ModelAndView searchPosts(@Valid @ModelAttribute("searchPostsForm") final SearchPostsForm searchPostsForm, BindingResult bindingResult) {

        if(bindingResult.hasErrors())
            throw new IllegalArgumentException("SearchController: Search params are invalid:" +
                    bindingResult.getAllErrors().stream().reduce("",
                            (acc, error) -> acc + " " + error.getObjectName() + " " + error.getDefaultMessage(), String::concat));

        final ModelAndView mv = new ModelAndView("search/posts");

        mv.addObject("query", searchPostsForm.getQuery());
        mv.addObject("posts",
                searchService.searchPosts(searchPostsForm.getQuery(), searchPostsForm.getPostCategory(), searchPostsForm.getPostAge(),
                        searchPostsForm.getSortCriteria(), searchPostsForm.getPageNumber(), searchPostsForm.getPageSize()));
        return mv;
    }

    @RequestMapping(path = "/search/movies/", method = RequestMethod.GET)
    public ModelAndView searchMovies(@ModelAttribute("searchMoviesForm") final SearchMoviesForm searchMoviesForm) {

        final ModelAndView mv = new ModelAndView("search/movies");
        mv.addObject("query", searchMoviesForm.getQuery());
        mv.addObject("movies",
                searchService.searchMovies(searchMoviesForm.getQuery()));
        return mv;
    }

    @RequestMapping(path = "/search/users/", method = RequestMethod.GET)
    public ModelAndView searchUsers(@ModelAttribute("searchUsersForm") final SearchUsersForm searchUsersForm) {

        final ModelAndView mv = new ModelAndView("search/users");

        mv.addObject("query", searchUsersForm.getQuery());
        mv.addObject("users",
                searchService.searchUsers(searchUsersForm.getQuery()));

        return mv;
    }

}

