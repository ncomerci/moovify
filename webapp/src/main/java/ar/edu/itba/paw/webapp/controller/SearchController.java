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

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public ModelAndView searchPosts(@RequestParam() final String query, @RequestParam(value = "filter_criteria[]", defaultValue = "by_post_title") Collection<String> filterCriteria,
                                    @RequestParam(value = "sort_criteria", defaultValue = "newest") final String sortCriteria) {

        final ModelAndView mv = new ModelAndView("search/posts/view");

        mv.addObject("query", query);
        mv.addObject("posts",
                searchService.findPostsBy(query, filterCriteria, sortCriteria, false, false)
                        .orElseThrow(NonExistingSearchCriteriaException::new));
        return mv;
    }
}

