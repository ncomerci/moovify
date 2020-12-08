package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class InvalidPostCategoryException extends CustomRuntimeException {

    public InvalidPostCategoryException() {
        super("Invalid post category");
        responseStatus = 400;
    }
}
