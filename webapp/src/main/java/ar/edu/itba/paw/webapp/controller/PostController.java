package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.MovieService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostCategory;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.InvalidPostCategoryException;
import ar.edu.itba.paw.webapp.exceptions.PostNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.CommentCreateForm;
import ar.edu.itba.paw.webapp.form.PostCreateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;


@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;


    @RequestMapping(path = "/post/{postId}", method = RequestMethod.GET)
    public ModelAndView view(@PathVariable final long postId,
                             @RequestParam(defaultValue = "5") final int pageSize,
                             @RequestParam(defaultValue = "0") final int pageNumber,
                             @ModelAttribute("CommentCreateForm") final CommentCreateForm commentCreateForm,
                             final HttpServletRequest request) {

        final ModelAndView mv = new ModelAndView("post/view");

        final Post post = getPostFromFlashParamsOrById(postId, request);

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(!isAnonymous(auth)) {
            final User user = userService.findUserByUsername(auth.getName()).orElseThrow(UserNotFoundException::new);

            mv.addObject("likeCurrentValue", userService.hasUserLikedPost(user, post));

            mv.addObject("loggedUser", user);
        }

        mv.addObject("post", post);
        mv.addObject("movies", movieService.findMoviesByPost(post));
        mv.addObject("comments", commentService.findPostCommentDescendants(post, pageNumber, pageSize));

        return mv;
    }

    @RequestMapping(path = "/post/like", method = RequestMethod.POST)
    public ModelAndView likePost(@RequestParam final long postId,
                                 @RequestParam(defaultValue = "0") final int value,
                                 final Principal principal) {

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
        final Post post = postService.findPostById(postId).orElseThrow(PostNotFoundException::new);

        postService.likePost(post, user, value);

        return new ModelAndView("redirect:/post/" + postId);
    }

    @RequestMapping(path = "/post/create", method = RequestMethod.GET)
    public ModelAndView showPostCreateForm(@ModelAttribute("postCreateForm") final PostCreateForm postCreateForm) {

        final ModelAndView mv = new ModelAndView("post/create");

        mv.addObject("movies", movieService.getAllMoviesNotPaginated());
        mv.addObject("categories", postService.getAllPostCategories());

        return mv;
    }

    @RequestMapping(path = "/post/create", method = RequestMethod.POST)
    public ModelAndView showPostCreateForm(@Valid @ModelAttribute("postCreateForm") final PostCreateForm postCreateForm,
                                           final BindingResult errors,
                                           final Principal principal,
                                           final RedirectAttributes redirectAttributes) {

        if (errors.hasErrors())
            return showPostCreateForm(postCreateForm);

        final User user = userService.findUserByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        final PostCategory postCategory = postService.findCategoryById(postCreateForm.getCategory())
                .orElseThrow(InvalidPostCategoryException::new);

        final Post post = postService.register(postCreateForm.getTitle(), postCreateForm.getBody(),
                postCategory, user,
                postCreateForm.getTags() == null ? Collections.emptySet() : postCreateForm.getTags(),
                postCreateForm.getMovies());

        redirectAttributes.addFlashAttribute("post", post);

        return new ModelAndView("redirect:/post/" + post.getId());
    }


    private boolean isAnonymous(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ANONYMOUS"));
    }

    private Post getPostFromFlashParamsOrById(long postId, HttpServletRequest request) {

        final Map<String, ?> flashParams = RequestContextUtils.getInputFlashMap(request);

        if(flashParams != null && flashParams.containsKey("post"))
            return (Post) flashParams.get("post");

        else
            return postService.findPostById(postId).orElseThrow(PostNotFoundException::new);
    }
}
