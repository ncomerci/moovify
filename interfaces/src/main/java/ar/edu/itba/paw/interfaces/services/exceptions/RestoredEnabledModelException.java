package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class RestoredEnabledModelException extends CustomException {

    public RestoredEnabledModelException() {
        super("Restored enabled model");
        responseStatus = 400;
    }
}
