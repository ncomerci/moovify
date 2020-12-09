package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class CommentNotFoundException extends CustomRuntimeException {

    public CommentNotFoundException() {
        super("error.commentNotFoundException", 404);
    }
}
