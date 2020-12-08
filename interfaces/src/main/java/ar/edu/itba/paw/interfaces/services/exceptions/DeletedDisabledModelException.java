package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class DeletedDisabledModelException extends CustomException {

    public DeletedDisabledModelException() {
        super("Deleted disabled model");
        responseStatus = 400;
    }
}
