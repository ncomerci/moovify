package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class LoggedUserAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggedUserAdvice.class);

    @Autowired
    private UserService userService;

    @ModelAttribute("loggedUser")
    public User loggedUser() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth.isAuthenticated() && !isAnonymous(auth)) {
            LOGGER.debug("Logged User was obtained by LoggedUserAdvice");
            return userService.findUserByUsername(auth.getName()).orElseThrow(UserNotFoundException::new);
        }

        LOGGER.debug("User is Anonymous");

        return null;
    }

    private boolean isAnonymous(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ANONYMOUS"));
    }
}
