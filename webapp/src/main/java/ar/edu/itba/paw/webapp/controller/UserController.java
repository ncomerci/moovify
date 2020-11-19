package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.persistence.exceptions.DuplicateUniqueUserAttributeException;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.*;
import ar.edu.itba.paw.webapp.form.ResetPasswordForm;
import ar.edu.itba.paw.webapp.form.UpdatePasswordForm;
import ar.edu.itba.paw.webapp.form.UserCreateForm;
import ar.edu.itba.paw.webapp.form.editProfile.AvatarEditForm;
import ar.edu.itba.paw.webapp.form.editProfile.ChangePasswordForm;
import ar.edu.itba.paw.webapp.form.editProfile.UserEditForm;
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
import java.util.EnumSet;
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
                                  final HttpServletRequest request) throws IOException {

        if(bindingResult.hasErrors()) {
            LOGGER.warn("Errors were found in the form userCreateForm creating a User");
            return showUserCreateForm(userCreateForm);
        }

        final User user;

        try {
            user = userService.register(userCreateForm.getUsername(),
                    userCreateForm.getPassword(), userCreateForm.getName(),
                    userCreateForm.getEmail(), userCreateForm.getDescription(), userCreateForm.getAvatar().getBytes(),
                    "confirmEmail", request.getLocale());
        }

        catch(DuplicateUniqueUserAttributeException e) {

            final EnumSet<DuplicateUniqueUserAttributeException.UniqueAttributes> duplicatedAttributes =
                    e.getDuplicatedUniqueAttributes();

            if(duplicatedAttributes.contains(DuplicateUniqueUserAttributeException.UniqueAttributes.USERNAME)) {
                bindingResult.rejectValue("username", "validation.user.UniqueUsername");

                LOGGER.warn("There was an error creating a User. Username {} was not unique", userCreateForm.getUsername());
            }

            if(duplicatedAttributes.contains(DuplicateUniqueUserAttributeException.UniqueAttributes.EMAIL)) {
                bindingResult.rejectValue("email", "validation.user.UniqueEmail");

                LOGGER.warn("There was an error creating a User. Email {} was not unique", userCreateForm.getEmail());
            }

            return showUserCreateForm(userCreateForm);
        }

        manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());

        LOGGER.info("User creation in /user/create was successful. Redirecting to /user/profile of User {}", user.getId());

        return new ModelAndView("redirect:/user/profile");
    }

    @RequestMapping(path = {"/user/{userId}", "/user/{userId}/posts"} , method = RequestMethod.GET)
    public ModelAndView viewPosts(@PathVariable final long userId,
                                  @RequestParam(defaultValue = "10") final int pageSize,
                                  @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /user/{}/posts", userId);

        final ModelAndView mv = new ModelAndView("user/view/viewPosts");

        final User user = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        mv.addObject("user", user);

        mv.addObject("posts", postService.findPostsByUser(user, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = "/user/{userId}/comments", method = RequestMethod.GET)
    public ModelAndView viewComments(@PathVariable final long userId,
                                     @RequestParam(defaultValue = "10") final int pageSize,
                                     @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /user/{}/comments", userId);

        final ModelAndView mv = new ModelAndView("user/view/viewComments");

        final User user = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        mv.addObject("user", user);

        mv.addObject("comments", commentService.findCommentsByUser(user, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = "/user/{userId}/followed/users", method = RequestMethod.GET)
    public ModelAndView viewFollowedUsers(@PathVariable final long userId,
                                         @RequestParam(defaultValue = "10") final int pageSize,
                                         @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /user/{}/followedUsers", userId);

        final ModelAndView mv = new ModelAndView("user/view/viewFollowedUsers");

        final User user = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        mv.addObject("user", user);

        mv.addObject("followedUsers", userService.getFollowedUsers(user, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = {"/user/profile", "/user/profile/posts"}, method = RequestMethod.GET)
    public ModelAndView profilePosts(@ModelAttribute("avatarEditForm") final AvatarEditForm avatarEditForm,
                                     @ModelAttribute("userEditForm") final UserEditForm userEditForm,
                                     final Principal principal,
                                     HttpServletRequest request,
                                     @RequestParam(defaultValue = "10") final int pageSize,
                                     @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /user/profile/posts");

        final ModelAndView mv = new ModelAndView("user/profile/profilePosts");

        loadUserEditBindingResultsToMv(request, mv);

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        mv.addObject("loggedUser", user);
        mv.addObject("posts", postService.findPostsByUser(user, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = {"/user/profile/favourite/posts"}, method = RequestMethod.GET)
    public ModelAndView profileFavouritePosts(@ModelAttribute("avatarEditForm") final AvatarEditForm avatarEditForm,
                                              @ModelAttribute("userEditForm") final UserEditForm userEditForm,
                                              final Principal principal,
                                              HttpServletRequest request,
                                              @RequestParam(defaultValue = "10") final int pageSize,
                                              @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /user/profile/favourite/posts");

        final ModelAndView mv = new ModelAndView("user/profile/profileFavouritePosts");

        loadUserEditBindingResultsToMv(request, mv);

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        mv.addObject("loggedUser", user);
        mv.addObject("posts", postService.getUserFavouritePosts(user, pageNumber, pageSize));

        return mv;
    }
    @RequestMapping(path = {"/user/profile/followed/users"}, method = RequestMethod.GET)
    public ModelAndView profileFollowedUsers(@ModelAttribute("avatarEditForm") final AvatarEditForm avatarEditForm,
                                             @ModelAttribute("userEditForm") final UserEditForm userEditForm,
                                             final Principal principal,
                                             final HttpServletRequest request,
                                             @RequestParam(defaultValue = "10") final int pageSize,
                                             @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /user/profile/followed/users");

        final ModelAndView mv = new ModelAndView("user/profile/profileFollowedUsers");

        loadUserEditBindingResultsToMv(request, mv);

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        mv.addObject("loggedUser", user);
        mv.addObject("followedUsers", userService.getFollowedUsers(user, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = "/user/profile/comments", method = RequestMethod.GET)
    public ModelAndView profileComments(@ModelAttribute("avatarEditForm") final AvatarEditForm avatarEditForm,
                                        @ModelAttribute("userEditForm") final UserEditForm userEditForm,
                                        final Principal principal,
                                        final HttpServletRequest request,
                                        @RequestParam(defaultValue = "10") final int pageSize,
                                        @RequestParam(defaultValue = "0") final int pageNumber) {

        LOGGER.info("Accessed /user/profile/comments");

        final ModelAndView mv = new ModelAndView("user/profile/profileComments");

        loadUserEditBindingResultsToMv(request, mv);

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        mv.addObject("loggedUser", user);
        mv.addObject("comments", commentService.findCommentsByUser(user, pageNumber, pageSize));

        return mv;
    }
    @RequestMapping(path = "/user/follow/{userId}", method = RequestMethod.POST)
    public ModelAndView followUser(@PathVariable final long userId,
                                   final Principal principal) {

        LOGGER.info("Accessed /user/follow");

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
        final User followedUser = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        if(!followedUser.isEnabled())
            throw new IllegalUserFollowException();

        userService.followUser(user, followedUser);
        return new ModelAndView("redirect:/user/" + userId);
    }

    @RequestMapping(path = "/user/unfollow/{userId}", method = RequestMethod.POST)
    public ModelAndView unfollowUser(@PathVariable final long userId,
                                   final Principal principal) {

        LOGGER.info("Accessed /user/follow");

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
        final User unfollowedUser = userService.findUserById(userId).orElseThrow(UserNotFoundException::new);

        if(!unfollowedUser.isEnabled())
            throw new IllegalUserUnfollowException();

        userService.unfollowUser(user, unfollowedUser);
        return new ModelAndView("redirect:/user/" + userId);
    }

    @RequestMapping(path = "/user/favourite/posts/add", method = RequestMethod.POST)
    public ModelAndView addFavouritePost(@RequestParam final long postId,
                                         final HttpServletRequest request, final Principal principal) {

        final Post post = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);
        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        userService.addFavouritePost(user, post);

        return new ModelAndView("redirect:" + request.getHeader("Referer"));
    }

    @RequestMapping(path = "/user/favourite/posts/remove", method = RequestMethod.POST)
    public ModelAndView removeFavouritePost(@RequestParam final long postId,
                                            final HttpServletRequest request, final Principal principal) {

        final Post post = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);
        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        userService.removeFavouritePost(user, post);

        return new ModelAndView("redirect:" + request.getHeader("Referer"));
    }

    @RequestMapping(path = "/user/edit", method = RequestMethod.POST)
    public ModelAndView generalUserEdit(@Valid @ModelAttribute("userEditForm") final UserEditForm userEditForm,
                                     final BindingResult bindingResult,
                                     @ModelAttribute("avatarEditForm") final AvatarEditForm avatarEditForm,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes,
                                     final Principal principal) {

        if(bindingResult.hasErrors()) {
            LOGGER.warn("Errors were found in the form userEditForm editing user in /user/edit");

            redirectAttributes.addFlashAttribute("userEditFormBindingResult", bindingResult);
            redirectAttributes.addFlashAttribute("userEditForm", userEditForm);

            return new ModelAndView("redirect:" + request.getHeader("Referer"));
        }

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        try {
            userService.generalUserUpdate(user, userEditForm.getName(), userEditForm.getUsername(), userEditForm.getDescription());
        }
        catch(DuplicateUniqueUserAttributeException e) {

            if(e.getDuplicatedUniqueAttributes().contains(DuplicateUniqueUserAttributeException.UniqueAttributes.USERNAME)) {
                bindingResult.rejectValue("username", "validation.user.UniqueUsername");

                LOGGER.warn("User {} tried to update it's username to a one which already existed, {}", user.getId(), userEditForm.getUsername());

                redirectAttributes.addFlashAttribute("userEditFormBindingResult", bindingResult);
                redirectAttributes.addFlashAttribute("userEditForm", userEditForm);

                return new ModelAndView("redirect:" + request.getHeader("Referer"));
            }
            else
                throw new IllegalStateException("DuplicateUniqueUserAttributeException had invalid state in updateUsername");
        }

        manualLogin(request, user.getUsername(), user.getPassword(), user.getRoles());

        LOGGER.info("Edited User's {} name successfully. Redirecting to /user/profile/edit", user.getId());

        return new ModelAndView("redirect:" + request.getHeader("Referer"));
    }

    private void loadUserEditBindingResultsToMv(HttpServletRequest request, ModelAndView mv) {
        Map<String, ?> flashAttr = RequestContextUtils.getInputFlashMap(request);

        if(flashAttr != null && flashAttr.containsKey("userEditFormBindingResult")) {
            mv.addObject("org.springframework.validation.BindingResult.userEditForm",
                    flashAttr.get("userEditFormBindingResult"));
        }
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
                                            @ModelAttribute("userEditForm") final UserEditForm userEditForm,
                                            final Principal principal,
                                            HttpServletRequest request) throws IOException {

        if(bindingResult.hasErrors()) {
            LOGGER.warn("Errors were found in the form avatarEditForm updating avatar in /user/profile/avatar");
            return profilePosts(avatarEditForm, userEditForm, principal, request, 5, 0);
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
    public ModelAndView resetPassword(@Valid @ModelAttribute("resetPasswordForm") final ResetPasswordForm resetPasswordForm, final BindingResult bindingResult,
                                      HttpServletRequest request) {

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

        userService.createPasswordResetEmail(user, "passwordResetEmail", request.getLocale());

        final ModelAndView mv = new ModelAndView("user/resetPassword/resetPasswordTokenGenerated");

        mv.addObject("loggedUser", user);

        LOGGER.info("Password reset successful by User {} in /user/resetPassword", user.getId());

        return mv;
    }

    @RequestMapping(path = "/user/resendConfirmation", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(Principal principal, HttpServletRequest request) {

        LOGGER.info("Accessed /user/resendConfirmation");

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        userService.createConfirmationEmail(user, "confirmEmail", request.getLocale());

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
        return roles.stream().map((role) -> new SimpleGrantedAuthority("ROLE_" + role.name())).collect(Collectors.toList());
    }
}
