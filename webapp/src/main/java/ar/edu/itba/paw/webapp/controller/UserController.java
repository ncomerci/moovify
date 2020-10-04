package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.InvalidResetPasswordToken;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.ResetPasswordForm;
import ar.edu.itba.paw.webapp.form.UpdatePasswordForm;
import ar.edu.itba.paw.webapp.form.UserCreateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

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
    private PostService postService;

    @Autowired
    private CommentService commentService;

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

        if(bindingResult.hasErrors())
            return showUserCreateForm(userCreateForm);

        final User user = userService.register(userCreateForm.getUsername(),
                userCreateForm.getPassword(), userCreateForm.getName(),
                userCreateForm.getEmail(), "confirmEmail");

        manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());

        redirectAttributes.addFlashAttribute("user", user);

        return new ModelAndView("redirect:/user/profile");
    }

    @RequestMapping(path = {"/user/{userId}", "/user/{userId}/posts"} , method = RequestMethod.GET)
    public ModelAndView viewPosts(HttpServletRequest request,
                                  @PathVariable final long userId,
                                  @RequestParam(defaultValue = "5") final int pageSize,
                                  @RequestParam(defaultValue = "0") final int pageNumber) {

        final ModelAndView mv = new ModelAndView("user/view/viewPosts");

        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        if(inputFlashMap == null || !inputFlashMap.containsKey("user"))
            mv.addObject("user", userService.findById(userId)
                .orElseThrow(UserNotFoundException::new));

        mv.addObject("posts", postService.findPostsByUserId(userId, pageNumber, pageSize));
        return mv;
    }

    @RequestMapping(path = "/user/{userId}/comments", method = RequestMethod.GET)
    public ModelAndView viewComments(HttpServletRequest request, @PathVariable final long userId,
                                     @RequestParam(defaultValue = "5") final int pageSize,
                                     @RequestParam(defaultValue = "0") final int pageNumber) {

        final ModelAndView mv = new ModelAndView("user/view/viewComments");

        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        if(inputFlashMap == null || !inputFlashMap.containsKey("user"))
            mv.addObject("user", userService.findById(userId)
                    .orElseThrow(UserNotFoundException::new));

        mv.addObject("comments", commentService.findCommentsByUserId(userId, pageNumber, pageSize));
        return mv;
    }

    @RequestMapping(path = {"/user/profile", "/user/profile/posts"}, method = RequestMethod.GET)
    public ModelAndView profilePosts(HttpServletRequest request, Principal principal,
                                     @RequestParam(defaultValue = "5") final int pageSize,
                                     @RequestParam(defaultValue = "0") final int pageNumber) {

        final ModelAndView mv = new ModelAndView("user/profile/profilePosts");

        User user;
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        if(inputFlashMap == null || !inputFlashMap.containsKey("user"))
            user = userService.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);
        else
            user = (User) inputFlashMap.get("user");

        mv.addObject("loggedUser", user);
        mv.addObject("posts", postService.findPostsByUserId(user.getId(), pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = "/user/profile/comments", method = RequestMethod.GET)
    public ModelAndView profileComments(Principal principal,
                                        @RequestParam(defaultValue = "5") final int pageSize,
                                        @RequestParam(defaultValue = "0") final int pageNumber) {

        final ModelAndView mv = new ModelAndView("user/profile/profileComments");

        final User user = userService.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        mv.addObject("loggedUser", user);
        mv.addObject("comments", commentService.findCommentsByUserId(user.getId(), pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = "/user/registrationConfirm", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(HttpServletRequest request, @RequestParam String token) {

        final Optional<User> optUser = userService.confirmRegistration(token);
        boolean success;

        ModelAndView mv = new ModelAndView("user/confirmRegistration/registrationConfirm");

        if(optUser.isPresent()) {
            success = true;
            final User user = optUser.get();

            mv.addObject("loggedUser", user);

            // User roles have been updates. We need to refresh authorities
            manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());
        }
        else
            success = false;

        mv.addObject("success", success);

        return mv;
    }

    @RequestMapping(path = "/user/resetPassword", method = RequestMethod.GET)
    public ModelAndView showResetPassword(@ModelAttribute("resetPasswordForm") final ResetPasswordForm resetPasswordForm) {
        return new ModelAndView("user/resetPassword/resetPassword");
    }

    @RequestMapping(path = "/user/resetPassword", method = RequestMethod.POST)
    public ModelAndView resetPassword(@Valid @ModelAttribute("resetPasswordForm") final ResetPasswordForm resetPasswordForm, final BindingResult bindingResult) {

        if(bindingResult.hasErrors())
            return showResetPassword(resetPasswordForm);

        final User user = userService.findByEmail(resetPasswordForm.getEmail()).orElseThrow(UserNotFoundException::new);

        userService.createPasswordResetEmail(user, "passwordResetEmail");

        final ModelAndView mv = new ModelAndView("user/resetPassword/resetPasswordTokenGenerated");

        mv.addObject("loggedUser", user);

        return mv;
    }

    @RequestMapping(path = "/user/resendConfirmation", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(Principal principal) {

        User user = userService.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        userService.createConfirmationEmail(user, "confirmEmail");

        ModelAndView mv = new ModelAndView("user/confirmRegistration/resendConfirmation");

        mv.addObject("loggedUser", user);

        return mv;
    }

    @RequestMapping(path = "/user/updatePassword/token", method = RequestMethod.GET)
    public ModelAndView validateResetPasswordToken(@RequestParam String token, RedirectAttributes redirectAttributes) {
        boolean validToken = userService.validatePasswordResetToken(token);

        if(validToken){
            redirectAttributes.addFlashAttribute("token", token);

            return new ModelAndView("redirect:/user/updatePassword");
        }

        return new ModelAndView("user/resetPassword/updatePasswordError");
    }

    @RequestMapping(path = "/user/updatePassword", method = RequestMethod.GET)
    public ModelAndView showUpdatePassword(@ModelAttribute("updatePasswordForm") final UpdatePasswordForm updatePasswordForm,
                                           HttpServletRequest request) {

        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        if(inputFlashMap != null && inputFlashMap.containsKey("token"))
            updatePasswordForm.setToken((String) inputFlashMap.get("token"));

        return new ModelAndView("user/resetPassword/updatePassword");
    }

    @RequestMapping(path = "/user/updatePassword", method = RequestMethod.POST)
    public ModelAndView updatePassword(@Valid @ModelAttribute("updatePasswordForm") final UpdatePasswordForm updatePasswordForm,
                                       final BindingResult bindingResult, HttpServletRequest request) {

        if(bindingResult.hasErrors())
            return showUpdatePassword(updatePasswordForm, request);

        User user = userService.updatePassword(updatePasswordForm.getPassword(), updatePasswordForm.getToken())
                .orElseThrow(InvalidResetPasswordToken::new);

        manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());

        ModelAndView mv = new ModelAndView("user/resetPassword/passwordResetSuccess");

        mv.addObject("loggedUser", user);

        return mv;
    }

    private void manualLogin(HttpServletRequest request, String username, String password, Collection<Role> roles) {

        PreAuthenticatedAuthenticationToken token =
                new PreAuthenticatedAuthenticationToken(username, password, getGrantedAuthorities(roles));

        // Parece que no hace falta
//        token.setDetails(new WebAuthenticationDetails(request));

        SecurityContextHolder.getContext().setAuthentication(token);

        //this step is important, otherwise the new login is not in session which is required by Spring Security
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }

    private Collection<GrantedAuthority> getGrantedAuthorities(Collection<Role> roles) {
        return roles.stream().map((role) -> new SimpleGrantedAuthority("ROLE_" + role.getRole())).collect(Collectors.toList());
    }
}
