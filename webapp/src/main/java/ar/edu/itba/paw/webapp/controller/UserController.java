package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        final ModelAndView mv = new ModelAndView("user/login");

        return mv;
    }

    @RequestMapping(path = "/user/create", method = RequestMethod.GET)
    public ModelAndView create() {
        final ModelAndView mv = new ModelAndView("user/create");

        return mv;
    }

    @RequestMapping(path = "/user/create", method = RequestMethod.POST)
    public ModelAndView register(@RequestParam final String username, @RequestParam final String password,
                                 @RequestParam final String name, @RequestParam final String email) {

        final User user = userService.register(username, password, name, email);

        // TODO: Solve better Post-Get with Flash Params
        return new ModelAndView("redirect:/user/" + user.getId());
    }

    @RequestMapping(path = "/user/{userId}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable final long userId, Principal principal) {

        final ModelAndView mv = new ModelAndView("user/view");

        mv.addObject("user", userService.findById(userId)
                .orElseThrow(UserNotFoundException::new));

        return mv;
    }

    @RequestMapping(path = "/user/profile", method = RequestMethod.GET)
    public ModelAndView view(Principal principal) {

        final ModelAndView mv = new ModelAndView("user/profile");

        mv.addObject("user", userService.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new));

        return mv;
    }


}
