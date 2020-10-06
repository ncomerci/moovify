package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

@Controller
public class AdminController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @RequestMapping(path = "/admin/deleted/posts", method = RequestMethod.GET)
    public ModelAndView deletedPosts(@RequestParam(defaultValue = "") String query) {

        ModelAndView mv = new ModelAndView("adminPanel/deleted/posts");
        mv.addObject("query", query);
        return mv;
    }

    @RequestMapping(path = "/admin/deleted/comments", method = RequestMethod.GET)
    public ModelAndView deletedComments(@RequestParam(defaultValue = "")String query) {

        ModelAndView mv = new ModelAndView("adminPanel/deleted/comments");
        mv.addObject("query", query);
        return mv;
    }

    @RequestMapping(path = "/admin/deleted/users", method = RequestMethod.GET)
    public ModelAndView deletedUsers(@RequestParam(defaultValue = "") String query) {

        ModelAndView mv = new ModelAndView("adminPanel/deleted/users");
        mv.addObject("query", query);
        return mv;
    }

    //    ================ COMMENTS PRIVILEGES ================

    @RequestMapping(path = "/comment/delete/{id}", method = RequestMethod.POST)
    public ModelAndView deleteComment(@PathVariable final long id, @RequestParam final long postId) {

        commentService.delete(id);

        return new ModelAndView("redirect:/post/" + postId);
    }

    //    ================ POSTS PRIVILEGES ================

    @RequestMapping(path = "/post/delete/{postId}", method = RequestMethod.POST)
    public ModelAndView deletePost(@PathVariable final long postId) {

        postService.delete(postId);

        return new ModelAndView("redirect:/");
    }

    //    ================ USERS PRIVILEGES ================

    @RequestMapping(path = "/user/promote/{id}", method = RequestMethod.POST)
    public ModelAndView promoteUser(@PathVariable long id) {

        userService.addRoles(id, Collections.singletonList("ADMIN"));
        return new ModelAndView("redirect:/user/" + id);
    }

    @RequestMapping(path = "/user/delete/{id}", method = RequestMethod.POST)
    public ModelAndView deleteUser(@PathVariable long id) {

        userService.delete(id);
        return new ModelAndView("redirect:/");
    }
}
