package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.exceptions.CommentNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.MovieNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.PostNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(PostNotFoundException.class)
    public ModelAndView handlePostNotFound(){
        ModelAndView mv = new ModelAndView("errorView");
        mv.addObject("message", messageSource.getMessage("error.postNotFoundException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "404" );
        return mv;
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ModelAndView handleMovieNotFound(){
        ModelAndView mv = new ModelAndView("errorView");
        mv.addObject("message", messageSource.getMessage("error.movieNotFoundException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "404" );
        return mv;
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ModelAndView handleCommentNotFound(){
        ModelAndView mv = new ModelAndView("errorView");
        mv.addObject("message", messageSource.getMessage("error.commentNotFoundException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "404" );
        return mv;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleError404(){
        ModelAndView mv = new ModelAndView("errorView");
        mv.addObject("message", messageSource.getMessage("error.noHandlerFoundException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "404" );
        return mv;
    }

    @Order
    @ExceptionHandler(Exception.class)
    public ModelAndView handleNonReachableState(){
        ModelAndView mv = new ModelAndView("errorView");
        mv.addObject("message", messageSource.getMessage("error.defaultMessage",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "500" );
        return mv;
    }
}
