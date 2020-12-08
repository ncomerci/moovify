package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class IllegalPostUnbookmarkException extends CustomException {

    public IllegalPostUnbookmarkException() {
        super("Illegal post unbookmark");
        responseStatus = 400;
    }
}
