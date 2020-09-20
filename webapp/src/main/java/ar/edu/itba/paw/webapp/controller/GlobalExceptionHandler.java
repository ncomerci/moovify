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

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(PostNotFoundException.class)
    public ModelAndView handlePostNotFound(){
        ModelAndView mv = new ModelAndView("error404");
        mv.addObject("message", messageSource.getMessage("error.postNotFoundException",null, LocaleContextHolder.getLocale()) );
        return mv;
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ModelAndView handleMovieNotFound(){
        ModelAndView mv = new ModelAndView("error404");
        mv.addObject("message", messageSource.getMessage("error.movieNotFoundException",null, LocaleContextHolder.getLocale()) );
        return mv;
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ModelAndView handleCommentNotFound(){
        ModelAndView mv = new ModelAndView("error404");
        mv.addObject("message", messageSource.getMessage("error.commentNotFoundException",null, LocaleContextHolder.getLocale()) );
        return mv;
    }

    @Order
    @ExceptionHandler(Throwable.class)
    public ModelAndView handleNonReachableState(){
        ModelAndView mv = new ModelAndView("error404");
        mv.addObject("message", messageSource.getMessage("error.defaultMessage",null, LocaleContextHolder.getLocale()) );
        return mv;
    }
}
