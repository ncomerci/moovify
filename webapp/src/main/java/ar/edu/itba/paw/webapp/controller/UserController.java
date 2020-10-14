package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateEmailException;
import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUsernameException;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.AvatarNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.InvalidResetPasswordToken;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.ResetPasswordForm;
import ar.edu.itba.paw.webapp.form.UpdatePasswordForm;
import ar.edu.itba.paw.webapp.form.UserCreateForm;
import ar.edu.itba.paw.webapp.form.editProfile.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public ModelAndView login() {

        LOGGER.info("Accessed /login");
        return new ModelAndView("user/login");
    }

    @RequestMapping(path = "/user/create", method = RequestMethod.GET)
    public ModelAndView showUserCreateForm(@ModelAttribute("userCreateForm") final UserCreateForm userCreateForm) {

        LOGGER.info("Accessed /user/create");
        return new ModelAndView("user/create");
    }

    @RequestMapping(path = "/user/create", method = RequestMethod.POST)
    public ModelAndView register(@Valid @ModelAttribute("userCreateForm") final UserCreateForm userCreateForm, final BindingResult bindingResult,
                                  final HttpServletRequest request, final RedirectAttributes redirectAttributes) throws IOException {

        if(bindingResult.hasErrors()) {
            LOGGER.warn("Errors were found in the form userCreateForm creating a User");
            return showUserCreateForm(userCreateForm);
        }

        final User user;

        try {
            user = userService.register(userCreateForm.getUsername(),
                    userCreateForm.getPassword(), userCreateForm.getName(),
                    userCreateForm.getEmail(), userCreateForm.getDescription(), userCreateForm.getAvatar().getBytes(), "confirmEmail");
        }

        catch(DuplicateUsernameException dupUsername) {
            bindingResult.rejectValue("username", "validation.user.UniqueUsername");

            LOGGER.warn("There was an error creating a User. Username {} was not unique", userCreateForm.getUsername());
            return showUserCreateForm(userCreateForm);
        }

        catch (DuplicateEmailException dupEmail) {
            bindingResult.rejectValue("email", "validation.user.UniqueEmail");

            LOGGER.warn("There was an error creating a User. Email {} was not unique", userCreateForm.getEmail());
            return showUserCreateForm(userCreateForm);
        }

        manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());

        redirectAttributes.addFlashAttribute("user", user);

        LOGGER.info("User creation in /user/create was successful. Redirecting to /user/profile of User {}", user.getId());

        return new ModelAndView("redirect:/user/profile");
    }

    @RequestMapping(path = {"/user/{userId}", "/user/{userId}/posts"} , method = RequestMethod.GET)
    public ModelAndView viewPosts(HttpServletRequest request,
                                  @PathVariable final long userId,
                                  @RequestParam(defaultValue = "10") final int pageSize,
                                  @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /user/{}/posts", userId);

        final ModelAndView mv = new ModelAndView("user/view/viewPosts");

        final User user = getUserFromFlashParamsOrById(userId, request);

        mv.addObject("user", user);

        mv.addObject("posts", postService.findPostsByUser(user, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = "/user/{userId}/comments", method = RequestMethod.GET)
    public ModelAndView viewComments(HttpServletRequest request,
                                     @PathVariable final long userId,
                                     @RequestParam(defaultValue = "10") final int pageSize,
                                     @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /user/{}/comments", userId);

        final ModelAndView mv = new ModelAndView("user/view/viewComments");

        final User user = getUserFromFlashParamsOrById(userId, request);

        mv.addObject("user", user);

        mv.addObject("comments", commentService.findCommentsByUser(user, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = {"/user/profile", "/user/profile/posts"}, method = RequestMethod.GET)
    public ModelAndView profilePosts(@ModelAttribute("avatarEditForm") final AvatarEditForm avatarEditForm,
                                     final HttpServletRequest request, final Principal principal,
                                     @RequestParam(defaultValue = "10") final int pageSize,
                                     @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /user/profile/posts");

        final ModelAndView mv = new ModelAndView("user/profile/profilePosts");

        final User user = getUserFromFlashParamsOrByUsername(principal.getName(), request);

        mv.addObject("loggedUser", user);
        mv.addObject("posts", postService.findPostsByUser(user, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = "/user/profile/comments", method = RequestMethod.GET)
    public ModelAndView profileComments(@ModelAttribute("avatarEditForm") final AvatarEditForm avatarEditForm, Principal principal,
                                        @RequestParam(defaultValue = "10") final int pageSize,
                                        @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /user/profile/comments");

        final ModelAndView mv = new ModelAndView("user/profile/profileComments");

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        mv.addObject("loggedUser", user);
        mv.addObject("comments", commentService.findCommentsByUser(user, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = "/user/profile/edit", method = RequestMethod.GET)
    public ModelAndView editProfile(@ModelAttribute("nameEditForm") final NameEditForm nameEditForm, @ModelAttribute("usernameEditForm") final UsernameEditForm usernameEditForm, @ModelAttribute("descriptionEditForm") final DescriptionEditForm descriptionEditForm) {

        LOGGER.info("Accessed /user/profile/edit");
        return new ModelAndView("user/profile/profileEdit");
    }

    @RequestMapping(path = "/user/edit/name", method = RequestMethod.POST)
    public ModelAndView editName(@Valid @ModelAttribute("nameEditForm") final NameEditForm nameEditForm, final BindingResult bindingResult,
                                 @ModelAttribute("usernameEditForm") final UsernameEditForm usernameEditForm,
                                 @ModelAttribute("descriptionEditForm") final DescriptionEditForm descriptionEditForm,
                                 final Principal principal) {

        if(bindingResult.hasErrors()) {
            LOGGER.warn("Errors were found in the form nameEditForm editing name in /user/edit/name");
            return editProfile(nameEditForm, usernameEditForm, descriptionEditForm);
        }

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        userService.updateName(user, nameEditForm.getName());

        LOGGER.info("Edited User's {} name successfully. Redirecting to /user/profile/edit", user.getId());

        return new ModelAndView("redirect:/user/profile/edit");
    }

    @RequestMapping(path = "/user/edit/username", method = RequestMethod.POST)
    public ModelAndView usernameEdit(@Valid @ModelAttribute("usernameEditForm") final UsernameEditForm usernameEditForm, final BindingResult bindingResult,
                                     @ModelAttribute("nameEditForm") final NameEditForm nameEditForm,
                                     @ModelAttribute("descriptionEditForm") final DescriptionEditForm descriptionEditForm,
                                     final HttpServletRequest request, final Principal principal) {

        if(bindingResult.hasErrors()) {
            LOGGER.warn("Errors were found in the form usernameEditForm editing username in /user/edit/username");
            return editProfile(nameEditForm, usernameEditForm, descriptionEditForm);
        }

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        try {
            userService.updateUsername(user, usernameEditForm.getUsername());
        }
        catch(DuplicateUsernameException e) {
            bindingResult.rejectValue("username", "validation.user.UniqueUsername");

            LOGGER.warn("User {} tried to update it's username to a one which already existed, {}", user.getId(), usernameEditForm.getUsername());

            return editProfile(nameEditForm, usernameEditForm, descriptionEditForm);
        }

        manualLogin(request, usernameEditForm.getUsername(), user.getPassword(), user.getRoles());

        LOGGER.info("Edited User's {} name successfully. Redirecting to /user/profile/edit", user.getId());

        return new ModelAndView("redirect:/user/profile/edit");
    }

    @RequestMapping(path = "/user/edit/description", method = RequestMethod.POST)
    public ModelAndView executeEdit(@Valid @ModelAttribute("descriptionEditForm") final DescriptionEditForm descriptionEditForm, final BindingResult bindingResult,
                                    @ModelAttribute("nameEditForm") final NameEditForm nameEditForm,
                                    @ModelAttribute("usernameEditForm") final UsernameEditForm usernameEditForm,
                                    Principal principal) {

        if(bindingResult.hasErrors()) {
            LOGGER.warn("Errors were found in the form descriptionEditForm editing description in /user/edit/description");
            return editProfile(nameEditForm, usernameEditForm, descriptionEditForm);
        }

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        userService.updateDescription(user, descriptionEditForm.getDescription());

        LOGGER.info("Edited User's {} description successfully. Redirecting to /user/profile/edit", user.getId());

        return new ModelAndView("redirect:/user/profile/edit");
    }

    @RequestMapping(path = "/user/changePassword", method = RequestMethod.GET)
    public ModelAndView changePassword(@ModelAttribute("changePasswordForm") final ChangePasswordForm changePasswordForm) {

        LOGGER.info("Accessed /user/changePassword");
        return new ModelAndView("user/profile/changePassword");
    }

    @RequestMapping(path = "/user/changePassword", method = RequestMethod.POST)
    public ModelAndView executeChangePassword(@Valid @ModelAttribute("changePasswordForm") final ChangePasswordForm changePasswordForm, final BindingResult bindingResult, Principal principal) {

        if(bindingResult.hasErrors()) {
            LOGGER.warn("Errors were found in the form changePasswordForm changing password in /user/changePassword");
            return changePassword(changePasswordForm);
        }

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        userService.updatePassword(user, changePasswordForm.getPassword());

        LOGGER.info("Changed User's {} password successfully. Redirecting to /user/profile", user.getId());

        return new ModelAndView("redirect:/user/profile");
    }

    @RequestMapping(path = "/user/profile/avatar", method = RequestMethod.POST)
    public ModelAndView updateAvatar(@Valid @ModelAttribute("avatarEditForm") final AvatarEditForm avatarEditForm,
                                            final BindingResult bindingResult,
                                            final HttpServletRequest request,
                                            final Principal principal) throws IOException {

        if(bindingResult.hasErrors()) {
            LOGGER.warn("Errors were found in the form avatarEditForm updating avatar in /user/profile/avatar");
            return profilePosts(avatarEditForm, request, principal, 5, 0);
        }


        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        userService.updateAvatar(user, avatarEditForm.getAvatar().getBytes());

        LOGGER.info("Changed User's {} password successfully. Redirecting to /user/profile", user.getId());

        return new ModelAndView("redirect:/user/profile");
    }

    @RequestMapping(path = "/user/registrationConfirm", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(HttpServletRequest request, @RequestParam String token) {

        LOGGER.info("Accessed /user/registrationConfirm");

        final Optional<User> optUser = userService.confirmRegistration(token);
        final boolean success;

        final ModelAndView mv = new ModelAndView("user/confirmRegistration/registrationConfirm");

        if(optUser.isPresent() && optUser.get().isEnabled()) {
            success = true;
            final User user = optUser.get();

            mv.addObject("loggedUser", user);

            // User roles have been updates. We need to refresh authorities
            manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());

            LOGGER.info("Successfully Confirmed User {} email", user.getId());
        }
        else {
            success = false;
            LOGGER.warn("User email confirmation failed");
        }

        mv.addObject("success", success);

        return mv;
    }

    @RequestMapping(path = "/user/resetPassword", method = RequestMethod.GET)
    public ModelAndView showResetPassword(@ModelAttribute("resetPasswordForm") final ResetPasswordForm resetPasswordForm) {
        LOGGER.info("Accessed /user/resetPassword");
        return new ModelAndView("user/resetPassword/resetPassword");
    }

    @RequestMapping(path = "/user/resetPassword", method = RequestMethod.POST)
    public ModelAndView resetPassword(@Valid @ModelAttribute("resetPasswordForm") final ResetPasswordForm resetPasswordForm, final BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            LOGGER.warn("Errors were found in the form resetPasswordForm updating avatar in /user/resetPassword");
            return showResetPassword(resetPasswordForm);
        }

        final Optional<User> optUser = userService.findUserByEmail(resetPasswordForm.getEmail());

        if(!optUser.isPresent()) {
            bindingResult.rejectValue("email", "validation.resetPassword.InvalidEmail");

            LOGGER.warn("Email provided to reset password doesn't belong to any User");
            return showResetPassword(resetPasswordForm);
        }

        final User user = optUser.get();

        if(!user.isValidated()) {
            bindingResult.rejectValue("email", "validation.resetPassword.EmailNotValidated");

            LOGGER.warn("Email provided to reset password wasn't validated by User {}", user.getId());
            return showResetPassword(resetPasswordForm);
        }

        userService.createPasswordResetEmail(user, "passwordResetEmail");

        final ModelAndView mv = new ModelAndView("user/resetPassword/resetPasswordTokenGenerated");

        mv.addObject("loggedUser", user);

        LOGGER.info("Password reset successful by User {} in /user/resetPassword", user.getId());

        return mv;
    }

    @RequestMapping(path = "/user/resendConfirmation", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(Principal principal) {

        LOGGER.info("Accessed /user/resendConfirmation");

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        userService.createConfirmationEmail(user, "confirmEmail");

        final ModelAndView mv = new ModelAndView("user/confirmRegistration/resendConfirmation");

        mv.addObject("loggedUser", user);

        return mv;
    }

    @RequestMapping(path = "/user/updatePassword/token", method = RequestMethod.GET)
    public ModelAndView validateResetPasswordToken(@RequestParam String token, RedirectAttributes redirectAttributes) {

        LOGGER.info("Accessed /user/updatePassword/token");

        final boolean validToken = userService.validatePasswordResetToken(token);

        if(validToken){
            redirectAttributes.addFlashAttribute("token", token);

            LOGGER.info("Token {} provided to reset Password is valid", token);

            return new ModelAndView("redirect:/user/updatePassword");
        }
        else
            LOGGER.warn("Token {} provided to reset Password is invalid", token);

        LOGGER.info("Redirecting to /user/resetPassword/updatePasswordError");

        return new ModelAndView("user/resetPassword/updatePasswordError");
    }

    @RequestMapping(path = "/user/updatePassword", method = RequestMethod.GET)
    public ModelAndView showUpdatePassword(@ModelAttribute("updatePasswordForm") final UpdatePasswordForm updatePasswordForm,
                                           final HttpServletRequest request) {

        LOGGER.info("Accessed /user/updatePassword");

        final Map<String, ?> flashParams = RequestContextUtils.getInputFlashMap(request);

        if(flashParams != null && flashParams.containsKey("token"))
            updatePasswordForm.setToken((String) flashParams.get("token"));

        return new ModelAndView("user/resetPassword/updatePassword");
    }

    @RequestMapping(path = "/user/updatePassword", method = RequestMethod.POST)
    public ModelAndView updatePassword(@Valid @ModelAttribute("updatePasswordForm") final UpdatePasswordForm updatePasswordForm,
                                       final BindingResult bindingResult, HttpServletRequest request) {

        if(bindingResult.hasErrors()) {
            LOGGER.warn("Errors were found in the form updatePasswordForm resetting password in /user/updatePassword");
            return showUpdatePassword(updatePasswordForm, request);
        }

        final User user = userService.updatePassword(updatePasswordForm.getPassword(), updatePasswordForm.getToken())
                .orElseThrow(InvalidResetPasswordToken::new);

        manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());

        final ModelAndView mv = new ModelAndView("user/resetPassword/passwordResetSuccess");

        mv.addObject("loggedUser", user);

        LOGGER.info("User {} resetted password successfully in /user/updatePassword", user.getId());

        return mv;
    }

    @RequestMapping(path = "/user/avatar/{avatarId}", method = RequestMethod.GET, produces = "image/*")
    public @ResponseBody byte[] getAvatar(@PathVariable long avatarId) {

        LOGGER.info("Accessed /user/avatar/{}", avatarId);
        return userService.getAvatar(avatarId).orElseThrow(AvatarNotFoundException::new);
    }

    private void manualLogin(HttpServletRequest request, String username, String password, Collection<Role> roles) {

        LOGGER.debug("Performing Manual Login (either to log a new user or refresh logged User credentials) for User [username={}; password={}; roles={}]", username, password, roles);

        final PreAuthenticatedAuthenticationToken token =
                new PreAuthenticatedAuthenticationToken(username, password, getGrantedAuthorities(roles));

        SecurityContextHolder.getContext().setAuthentication(token);

        //this step is important, otherwise the new login is not in session which is required by Spring Security
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }

    private Collection<GrantedAuthority> getGrantedAuthorities(Collection<Role> roles) {
        return roles.stream().map((role) -> new SimpleGrantedAuthority("ROLE_" + role.getRole())).collect(Collectors.toList());
    }

    private User getUserFromFlashParamsOrById(long userId, HttpServletRequest request) {

        final Map<String, ?> flashParams = RequestContextUtils.getInputFlashMap(request);

        if(flashParams != null && flashParams.containsKey("user")) {
            LOGGER.debug("Obtained User from Flash Params");
            return (User) flashParams.get("user");
        }

        else
            return userService.findUserById(userId).orElseThrow(UserNotFoundException::new);
    }

    private User getUserFromFlashParamsOrByUsername(String username, HttpServletRequest request) {

        final Map<String, ?> flashParams = RequestContextUtils.getInputFlashMap(request);

        if(flashParams != null && flashParams.containsKey("user")) {
            LOGGER.debug("Obtained User from Flash Params");
            return (User) flashParams.get("user");
        }

        else
            return userService.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
    }
}
