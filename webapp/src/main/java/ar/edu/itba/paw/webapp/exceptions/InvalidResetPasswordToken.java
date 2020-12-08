package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;

public class InvalidResetPasswordToken extends CustomRuntimeException {

    public InvalidResetPasswordToken() {
        super("Invalid reset password token");
        responseStatus = 400;
    }
}
