package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.exceptions.CommentNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.MovieNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.PostNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PostNotFoundException.class)
    public ModelAndView handlePostNotFound(HttpServletResponse response){
        ModelAndView mv = new ModelAndView("errorView");
        mv.addObject("message", messageSource.getMessage("error.postNotFoundException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "404" );
        return mv;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MovieNotFoundException.class)
    public ModelAndView handleMovieNotFound(){
        ModelAndView mv = new ModelAndView("errorView");
        mv.addObject("message", messageSource.getMessage("error.movieNotFoundException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "404" );
        return mv;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CommentNotFoundException.class)
    public ModelAndView handleCommentNotFound(){
        ModelAndView mv = new ModelAndView("errorView");
        mv.addObject("message", messageSource.getMessage("error.commentNotFoundException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "404" );
        return mv;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleError404(){
        ModelAndView mv = new ModelAndView("errorView");
        mv.addObject("message", messageSource.getMessage("error.noHandlerFoundException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "404" );
        return mv;
    }

    @Order
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleNonReachableState(){
        ModelAndView mv = new ModelAndView("errorView");
        mv.addObject("message", messageSource.getMessage("error.defaultMessage",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "500" );
        return mv;
    }
}
