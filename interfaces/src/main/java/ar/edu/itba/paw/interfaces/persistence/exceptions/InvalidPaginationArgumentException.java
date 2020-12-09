package ar.edu.itba.paw.interfaces.persistence.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class InvalidPaginationArgumentException extends CustomRuntimeException {

    public InvalidPaginationArgumentException() {
        super("error.invalidPaginationArgumentException", 400);
    }
}
