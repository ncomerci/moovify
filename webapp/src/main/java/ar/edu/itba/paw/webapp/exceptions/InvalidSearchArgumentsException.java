package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class InvalidSearchArgumentsException extends CustomRuntimeException {

    public InvalidSearchArgumentsException() {
        super("error.invalidSearchArgumentsException", 400);
    }
}
