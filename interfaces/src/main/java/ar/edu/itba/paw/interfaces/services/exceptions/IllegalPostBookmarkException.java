package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class IllegalPostBookmarkException extends CustomException {

    public IllegalPostBookmarkException() {
        super("Illegal post bookmark");
        responseStatus = 400;
    }
}
