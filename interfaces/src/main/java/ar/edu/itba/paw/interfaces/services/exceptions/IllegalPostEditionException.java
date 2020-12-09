package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class IllegalPostEditionException extends CustomException {

    public IllegalPostEditionException() {
        super("error.illegalPostEditionException", 400);
    }
}
