package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class IllegalUserFollowException extends CustomException {

    public IllegalUserFollowException() {
        super("error.illegalUserFollowException", 400);
    }
}
