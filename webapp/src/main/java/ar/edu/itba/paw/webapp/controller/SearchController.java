package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @RequestMapping( path ="/search", method = RequestMethod.GET)
    public ModelAndView searchPosts(@RequestParam() final String searchParam){

        final ModelAndView mv = new ModelAndView( "search/posts/view");
        mv.addObject("posts", searchService.searchPosts(searchParam));
        return mv;
    }

    @RequestMapping( path ="/searchposttitle", method = RequestMethod.GET)
    public ModelAndView searchPostByTitle(@RequestParam() final String title){

        final ModelAndView mv = new ModelAndView( "search/posts/view");
        mv.addObject("posts", searchService.searchPostsbyTitle(title));
        mv.addObject("query",title);
        return mv;
    }
    @RequestMapping( path ="/searchmovieid", method = RequestMethod.GET)
    public ModelAndView searchPostByMovieId(@RequestParam() final long movie_id){

        final ModelAndView mv = new ModelAndView( "search/posts/view");
        mv.addObject("posts", searchService.searchPostsbyMovieId(movie_id));
        return mv;
    }

    @RequestMapping( path ="/searchmovietitle", method = RequestMethod.GET)
    public ModelAndView searchPostByMovieTitle(@RequestParam() final String movie_title){

        final ModelAndView mv = new ModelAndView( "search/posts/view");
        mv.addObject("posts", searchService.searchPostsbyMovieTitle(movie_title));
        mv.addObject("query",movie_title);
        return mv;
    }

}
