package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class InvalidSearchArgumentsException extends CustomRuntimeException {

    public InvalidSearchArgumentsException() {
        super("Invalid search arguments");
        responseStatus = 400;
    }
}
