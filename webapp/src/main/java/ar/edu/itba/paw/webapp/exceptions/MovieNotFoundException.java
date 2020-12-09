package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class MovieNotFoundException extends CustomRuntimeException {

    public MovieNotFoundException() {
        super("error.movieNotFoundException", 404);
    }
}
