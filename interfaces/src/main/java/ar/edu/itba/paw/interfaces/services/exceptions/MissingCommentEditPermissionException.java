package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class MissingCommentEditPermissionException extends CustomException {

    public MissingCommentEditPermissionException() {
        super("error.missingCommentEditPermissionException", 403);
    }
}
