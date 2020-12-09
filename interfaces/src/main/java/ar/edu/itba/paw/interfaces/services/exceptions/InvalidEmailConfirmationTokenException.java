package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class InvalidEmailConfirmationTokenException extends CustomException {

    public InvalidEmailConfirmationTokenException() {
        super("error.invalidEmailConfirmationTokenException", 400);
    }
}
