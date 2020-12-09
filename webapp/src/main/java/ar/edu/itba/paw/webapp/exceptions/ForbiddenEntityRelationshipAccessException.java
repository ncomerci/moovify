package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class ForbiddenEntityRelationshipAccessException extends CustomRuntimeException {

    public ForbiddenEntityRelationshipAccessException(boolean isAuthenticated) {
        super("error.forbiddenEntityRelationshipAccessException", isAuthenticated? 403 : 401);
    }
}
