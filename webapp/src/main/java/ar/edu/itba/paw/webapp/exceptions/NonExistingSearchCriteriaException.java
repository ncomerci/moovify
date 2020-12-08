package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class NonExistingSearchCriteriaException extends CustomRuntimeException {

    public NonExistingSearchCriteriaException() {
        super("Non existing search criteria");
        responseStatus = 400;
    }
}
