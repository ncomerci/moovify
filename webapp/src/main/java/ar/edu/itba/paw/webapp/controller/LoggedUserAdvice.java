package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class LoggedUserAdvice {

    @Autowired
    private UserService userService;

    @ModelAttribute("loggedUser")
    public User loggedUser(Model model) {

        if(model.containsAttribute("loggedUser"))
            return (User) model.asMap().get("loggedUser");

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth.isAuthenticated() && !isAnonymous(auth))
            return userService.findByUsername(auth.getName()).orElseThrow(UserNotFoundException::new);

        return null;
    }

    private boolean isAnonymous(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ANONYMOUS"));
    }
}
