package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class UserNotFoundException extends CustomRuntimeException {

    public UserNotFoundException() {
        super("User not found");
        responseStatus = 404;
    }
}
