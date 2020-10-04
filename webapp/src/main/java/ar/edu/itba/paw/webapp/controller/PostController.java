package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.MovieService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
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

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collections;


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
                             @ModelAttribute("CommentCreateForm") final CommentCreateForm commentCreateForm) {

        final ModelAndView mv = new ModelAndView("post/view");

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        mv.addObject("post", postService.findPostById(postId).orElseThrow(PostNotFoundException::new));
        mv.addObject("movies", movieService.findMoviesByPostId(postId));
        mv.addObject("comments", commentService.findPostCommentDescendants(postId, pageNumber, pageSize));

        if(!isAnonymous(auth))
            mv.addObject("isPostLiked", userService.hasUserLiked(auth.getName(), postId));

        return mv;
    }

    @RequestMapping(path = "/post/like", method = RequestMethod.POST )
    public ModelAndView showPostCreateForm(@RequestParam final long postId,
                                           @RequestParam(defaultValue = "false") final boolean value,
                                           final Principal principal) {

        User user = userService.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        postService.likePost(postId, user.getId(), value);

        return new ModelAndView("redirect:/post/" + postId);
    }

    @RequestMapping(path = "/post/create", method = RequestMethod.GET )
    public ModelAndView showPostCreateForm(@ModelAttribute("postCreateForm") final PostCreateForm postCreateForm) {

        final ModelAndView mv = new ModelAndView("post/create");

        mv.addObject("movies", movieService.getAllMoviesNotPaginated());
        mv.addObject("categories", postService.getAllPostCategories());

        return mv;
    }

    @RequestMapping(path = "/post/create", method = RequestMethod.POST )
    public ModelAndView showPostCreateForm(@Valid @ModelAttribute("postCreateForm") final PostCreateForm postCreateForm, final BindingResult errors, final Principal principal) {

        if (errors.hasErrors())
            return showPostCreateForm(postCreateForm);

        User user = userService.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);

        final long post = postService.register(postCreateForm.getTitle(), postCreateForm.getBody(),
                postCreateForm.getCategory(), user.getId(),
                postCreateForm.getTags() == null ? Collections.emptySet() : postCreateForm.getTags(),
                postCreateForm.getMovies());

        return new ModelAndView("redirect:/post/" + post);
    }


    private boolean isAnonymous(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ANONYMOUS"));
    }
}
