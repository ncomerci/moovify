package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class PayloadRequiredException extends CustomRuntimeException {

    public PayloadRequiredException() {
        super("error.payloadRequiredException", 400);
    }
}
