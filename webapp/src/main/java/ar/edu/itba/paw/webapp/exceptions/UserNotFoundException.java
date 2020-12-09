package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class UserNotFoundException extends CustomRuntimeException {

    public UserNotFoundException() {
        super("error.userNotFoundException", 404);
    }
}
