package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class InvalidSortCriteriaException extends CustomRuntimeException {

    public InvalidSortCriteriaException() {
        super("Invalid sort criteria");
        responseStatus = 400;
    }
}
