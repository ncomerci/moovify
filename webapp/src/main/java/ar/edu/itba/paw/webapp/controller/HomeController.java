//package ar.edu.itba.paw.webapp.controller;
//
//import ar.edu.itba.paw.interfaces.services.HomeService;
//import ar.edu.itba.paw.interfaces.services.UserService;
//import ar.edu.itba.paw.models.User;
//import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.MessageSource;
//import org.springframework.context.i18n.LocaleContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.security.Principal;
//
//@Controller
//public class HomeController {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
//
//    @Autowired
//    private HomeService homeService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private MessageSource messageSource;
//
//    @RequestMapping(path = {"/", "/hottest"}, method = RequestMethod.GET)
//    public ModelAndView indexHottest(@RequestParam(defaultValue = "35") final int pageSize,
//                                   @RequestParam(defaultValue = "0") final int pageNumber) {
//
//        LOGGER.info("Accessed /. Hello World!");
//
//        final ModelAndView mv = new ModelAndView("home/hottestPosts");
//
//        mv.addObject("hottestPosts", homeService.getHottestPosts(pageNumber, pageSize));
//        mv.addObject("hottestUsers", homeService.getHottestUsers());
//
//        return mv;
//    }
//
//    @RequestMapping(path = "/newest", method = RequestMethod.GET)
//    public ModelAndView indexNewest(@RequestParam(defaultValue = "35") final int pageSize,
//                                    @RequestParam(defaultValue = "0") final int pageNumber) {
//
//        LOGGER.info("Accessed /newest with pageSize = {} and pageNumber = {}", pageSize, pageNumber);
//
//        final ModelAndView mv = new ModelAndView("home/newestPosts");
//
//        mv.addObject("newestPosts", homeService.getNewestPosts(pageNumber, pageSize));
//        mv.addObject("hottestUsers", homeService.getHottestUsers());
//
//        return mv;
//    }
//
//    @RequestMapping(path = "/feed", method = RequestMethod.GET)
//    public ModelAndView indexFeed(Principal principal,
//                                  @RequestParam(defaultValue = "35") final int pageSize,
//                                  @RequestParam(defaultValue = "0") final int pageNumber) {
//
//        LOGGER.info("Accessed /feed with pageSize = {} and pageNumber = {}", pageSize, pageNumber);
//
//        final ModelAndView mv = new ModelAndView("home/feed");
//
//        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
//
//        mv.addObject("followedUsersPosts", homeService.getFollowedUsersPosts(user, pageNumber, pageSize));
//        mv.addObject("hottestUsers", homeService.getHottestUsers());
//
//        return mv;
//    }
//
//
//    @RequestMapping(path = "/403")
//    public ModelAndView accessDenied() {
//
//        final ModelAndView mv = new ModelAndView("errorView");
//
//        mv.addObject("message", messageSource.getMessage("error.accessDeniedException",null, LocaleContextHolder.getLocale()));
//        mv.addObject("code", "403" );
//
//        LOGGER.warn("A resource the current user was not authorized to access war requested. Responding with Http Status 403");
//
//        return mv;
//    }
//}