package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class MissingPostEditPermissionException extends CustomException {

    public MissingPostEditPermissionException() {
        super("error.missingPostEditPermissionException", 403);
    }
}
