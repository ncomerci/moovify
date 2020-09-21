package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.UserCreateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        return new ModelAndView("user/login");
    }

    @RequestMapping(path = "/user/create", method = RequestMethod.GET)
    public ModelAndView showUserCreateForm(@ModelAttribute("userCreateForm") final UserCreateForm userCreateForm) {

        return new ModelAndView("user/create");
    }

    @RequestMapping(path = "/user/create", method = RequestMethod.POST)
    public ModelAndView register(@Valid @ModelAttribute("userCreateForm") final UserCreateForm userCreateForm, final BindingResult bindingResult,
                                  HttpServletRequest request, final RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()){
            return showUserCreateForm(userCreateForm);
        }

        final User user = userService.register(userCreateForm.getUsername(),
                userCreateForm.getPassword(), userCreateForm.getName(), userCreateForm.getEmail());

        createVerificationToken(user, request);

        manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());

        redirectAttributes.addFlashAttribute("user", user);

        return new ModelAndView("redirect:/user/" + user.getId());
    }

    @RequestMapping(path = "/user/{userId}", method = RequestMethod.GET)
    public ModelAndView view(HttpServletRequest request, @PathVariable final long userId) {

        final ModelAndView mv = new ModelAndView("user/view");

        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        if(inputFlashMap == null || !inputFlashMap.containsKey("user"))
            mv.addObject("user", userService.findById(userId)
                .orElseThrow(UserNotFoundException::new));

        return mv;
    }

    @RequestMapping(path = "/user/profile", method = RequestMethod.GET)
    public ModelAndView profile(Principal principal) {

        final ModelAndView mv = new ModelAndView("user/profile");

        mv.addObject("user", userService.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new));

        return mv;
    }

    // TODO: Hacer vista
    @RequestMapping(path = "/user/registrationConfirm", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(HttpServletRequest request, @RequestParam String token, Authentication authentication) {

        Optional<User> optUser = userService.confirmRegistration(token);
        boolean success = true;
        User user;

        ModelAndView mv = new ModelAndView("user/registrationConfirm");

        if(!optUser.isPresent()) {
            mv.addObject("errorMessage", "Confirmation link was invalid");
            success = false;
        }

        else {
            user = optUser.get();
            mv.addObject("user", user);

            if (!optUser.get().hasRole("USER")) {
                mv.addObject("errorMessage", "Confirmation link was expired");
                success = false;
            }

            // User roles have been updates. We need to refresh authorities
            else
                manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());

        }

        mv.addObject("success", success);

        return mv;
    }

    // TODO: Hacer vista
    // TODO: Migrate to Spring Form. Validate username with userService.userExistsAndIsNotValidated(username)
    @RequestMapping(path = "/user/resendConfirmation", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(HttpServletRequest request, Principal principal) {

        // TODO: Validate url with Spring Security
        User user = userService.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        createVerificationToken(user, request);

        ModelAndView mv = new ModelAndView("user/resendConfirmation");

        mv.addObject("user", user);

        return mv;
    }

    private void createVerificationToken(User user, HttpServletRequest request) {
        final String token = userService.createVerificationToken(user.getId());

        mailService.sendSimpleEmail(user.getEmail(), "Confirmation Email",
                ServletUriComponentsBuilder.fromRequestUri(request)
                        .replacePath("/user/registrationConfirm").queryParam("token", token).build().toUriString());
    }

    private void manualLogin(HttpServletRequest request, String username, String password, Collection<Role> roles) {

        PreAuthenticatedAuthenticationToken token =
                new PreAuthenticatedAuthenticationToken(username, password, getGrantedAuthorities(roles));

        token.setDetails(new WebAuthenticationDetails(request));

        SecurityContextHolder.getContext().setAuthentication(token);

        //this step is important, otherwise the new login is not in session which is required by Spring Security
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }

    private Collection<GrantedAuthority> getGrantedAuthorities(Collection<Role> roles) {
        return roles.stream().map((role) -> new SimpleGrantedAuthority("ROLE_" + role.getRole())).collect(Collectors.toList());
    }
}
