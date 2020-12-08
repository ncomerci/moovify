package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class PostNotFoundException extends CustomRuntimeException {

    public PostNotFoundException() {
        super("Post not found");
        responseStatus = 404;
    }
}
