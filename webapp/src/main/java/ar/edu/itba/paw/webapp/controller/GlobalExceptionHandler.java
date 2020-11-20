package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.persistence.exceptions.InvalidMovieIdException;
import ar.edu.itba.paw.interfaces.persistence.exceptions.InvalidPaginationArgumentException;
import ar.edu.itba.paw.interfaces.services.exceptions.*;
import ar.edu.itba.paw.webapp.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private MessageSource messageSource;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PostNotFoundException.class)
    public ModelAndView handlePostNotFound() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.postNotFoundException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "404" );

        LOGGER.error("PostNotFoundException was thrown. Responding with Http Status 404");

        return mv;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MovieNotFoundException.class)
    public ModelAndView handleMovieNotFound() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.movieNotFoundException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "404" );

        LOGGER.error("MovieNotFoundException was thrown. Responding with Http Status 404");

        return mv;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CommentNotFoundException.class)
    public ModelAndView handleCommentNotFound() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.commentNotFoundException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "404" );

        LOGGER.error("CommentNotFoundException was thrown. Responding with Http Status 404");

        return mv;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFound() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.userNotFoundException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "404" );

        LOGGER.error("UserNotFoundException was thrown. Responding with Http Status 404");

        return mv;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AvatarNotFoundException.class)
    public void handleAvatarNotFound() {
        LOGGER.error("AvatarNotFoundException was thrown. Responding with Http Status 404");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleError404() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.noHandlerFoundException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "404" );

        LOGGER.error("A non-existent resource was requested. Responding with Http Status 404");

        return mv;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(MissingPostEditPermissionException.class)
    public ModelAndView handleMissingPostEditPermission() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.missingPostEditPermissionException",null, LocaleContextHolder.getLocale()));
        mv.addObject("code", "403" );

        LOGGER.error("A user tried to edit a post which didn't own. Responding with Http Status 403");

        return mv;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(MissingCommentEditPermissionException.class)
    public ModelAndView handleMissingCommentEditPermission() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.missingCommentEditPermissionException",null, LocaleContextHolder.getLocale()));
        mv.addObject("code", "403" );

        LOGGER.error("A user tried to edit a comment which didn't own. Responding with Http Status 403");

        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPostCategoryException.class)
    public ModelAndView handleInvalidPostCategory() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.invalidPostCategoryException",null, LocaleContextHolder.getLocale()));
        mv.addObject("code", "400" );

        LOGGER.error("InvalidPostCategoryException was thrown. Responding with Http Status 400");

        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidMovieIdException.class)
    public ModelAndView handleInvalidMovieId() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.invalidMovieIdException",null, LocaleContextHolder.getLocale()));
        mv.addObject("code", "400" );

        LOGGER.error("InvalidMovieIdException was thrown. Responding with Http Status 400");

        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidResetPasswordToken.class)
    public ModelAndView handleInvalidResetPasswordToken() {
        LOGGER.error("InvalidResetPasswordToken was thrown. Responding with Http Status 400");

        return new ModelAndView("redirect:/");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NonExistingSearchCriteriaException.class)
    public ModelAndView handleNonExistingSearchCriteria() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.nonExistingSearchCriteriaException",null, LocaleContextHolder.getLocale()));
        mv.addObject("code", "400" );

        LOGGER.error("NonExistingSearchCriteriaException was thrown. Responding with Http Status 400");

        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidSearchArgumentsException.class)
    public ModelAndView handleInvalidSearchArguments() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.invalidSearchArgumentsException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "400" );

        LOGGER.error("InvalidSearchArgumentsException was thrown. Responding with Http Status 400");

        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPaginationArgumentException.class)
    public ModelAndView handleInvalidPaginationArgument() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.invalidPaginationArgumentException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "400" );

        LOGGER.error("InvalidPaginationArgumentException was thrown. Responding with Http Status 400");

        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DeletedDisabledModelException.class)
    public ModelAndView handleDeletedDisabledModel() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.DeletedDisabledModelException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "400" );

        LOGGER.error("DeletedDisabledModelException was thrown. Responding with Http Status 400");

        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalCommentLikeException.class)
    public ModelAndView handleIllegalCommentLike() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.IllegalCommentLikeException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "400" );

        LOGGER.error("IllegalCommentLikeException was thrown. Responding with Http Status 400");

        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalPostLikeException.class)
    public ModelAndView handleIllegalPostLike() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.IllegalPostLikeException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "400" );

        LOGGER.error("IllegalPostLikeException was thrown. Responding with Http Status 400");

        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidUserPromotionException.class)
    public ModelAndView handleInvalidUserPromotion() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.InvalidUserPromotionException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "400" );

        LOGGER.error("InvalidUserPromotionException was thrown. Responding with Http Status 400");

        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RestoredEnabledModelException.class)
    public ModelAndView handleRestoredEnabledModel() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.RestoredEnabledModelException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "400" );

        LOGGER.error("RestoredEnabledModelException was thrown. Responding with Http Status 400");

        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalUserFollowException.class)
    public ModelAndView handleIllegalUserFollow() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.illegalUserFollowException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "400" );

        LOGGER.error("IllegalUserFollowException was thrown. Responding with Http Status 400");

        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalUserUnfollowException.class)
    public ModelAndView handleIllegalUserUnfollow() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.illegalUserUnfollowException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "400" );

        LOGGER.error("IllegalUserUnfollowException was thrown. Responding with Http Status 400");

        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalCommentEditionException.class)
    public ModelAndView handleIllegalCommentEdition() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.illegalCommentEditionException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "400" );

        LOGGER.error("IllegalCommentEditionException was thrown. Responding with Http Status 400");

        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalPostEditionException.class)
    public ModelAndView handleIllegalPostEdition() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.illegalPostEditionException",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "400" );

        LOGGER.error("IllegalPostEditionException was thrown. Responding with Http Status 400");

        return mv;
    }

    @Order
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleNonReachableState() {

        final ModelAndView mv = new ModelAndView("errorView");

        mv.addObject("message", messageSource.getMessage("error.defaultMessage",null, LocaleContextHolder.getLocale()) );
        mv.addObject("code", "500" );

        LOGGER.error("There was an internal error in the application. Responding with Http Status 500");

        return mv;
    }
}
