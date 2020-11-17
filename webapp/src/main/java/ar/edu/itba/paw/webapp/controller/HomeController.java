package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    private static final int HOME_PAGE_POST_COUNT = 10;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ModelAndView helloWorld(Principal principal) {

        LOGGER.info("Accessed /. Hello World!");

        final ModelAndView mv = new ModelAndView("index");
        mv.addObject("newestPosts", postService.getAllPostsOrderByNewest(0, HOME_PAGE_POST_COUNT));
        mv.addObject("hottestPosts", postService.getAllPostsOrderByHottest(0, HOME_PAGE_POST_COUNT));

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth.isAuthenticated() && !isAnonymous(auth)) {
            final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
            mv.addObject("followedUsersPosts", postService.getFollowedUsersPosts(user, 0, HOME_PAGE_POST_COUNT));
        }

        return mv;
    }

    @RequestMapping(path = "/403")
    public ModelAndView accessDenied() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.accessDeniedException",null, LocaleContextHolder.getLocale()));
        mv.addObject("code", "403" );

        LOGGER.warn("A resource the current user was not authorized to access war requested. Responding with Http Status 403");

        return mv;
    }

    private boolean isAnonymous(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ANONYMOUS"));
    }
}