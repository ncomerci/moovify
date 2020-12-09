package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class IllegalPostBookmarkException extends CustomException {

    public IllegalPostBookmarkException() {
        super("error.illegalPostBookmarkException", 400);
    }
}
