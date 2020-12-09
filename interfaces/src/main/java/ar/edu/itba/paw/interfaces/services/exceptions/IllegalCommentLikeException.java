package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class IllegalCommentLikeException extends CustomException {

    public IllegalCommentLikeException() {
        super("error.IllegalCommentLikeException", 400);
    }
}
