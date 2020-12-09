package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class InvalidResetPasswordToken extends CustomException {

    public InvalidResetPasswordToken() {
        super("error.invalidResetPasswordTokenException", 400);
    }
}
