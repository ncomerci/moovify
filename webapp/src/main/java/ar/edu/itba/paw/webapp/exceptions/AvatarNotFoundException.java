package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class AvatarNotFoundException extends CustomRuntimeException {

    public AvatarNotFoundException() {
        super("Avatar not found");
        responseStatus = 404;
    }
}
