package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class IllegalCommentEditionException extends CustomException {

    public IllegalCommentEditionException() {
        super("Illegal comment edition");
        responseStatus = 400;
    }
}
