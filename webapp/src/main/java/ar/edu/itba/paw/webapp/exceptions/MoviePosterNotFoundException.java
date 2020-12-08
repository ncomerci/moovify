package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class MoviePosterNotFoundException extends CustomRuntimeException {

    public MoviePosterNotFoundException() {
        super("Movie poster not found");
        responseStatus = 404;
    }
}
