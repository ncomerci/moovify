package ar.edu.itba.paw.interfaces.persistence.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class InvalidMovieIdException extends CustomRuntimeException {

    public InvalidMovieIdException() {
        super("Invalid movie id");
        responseStatus = 400;
    }
}
