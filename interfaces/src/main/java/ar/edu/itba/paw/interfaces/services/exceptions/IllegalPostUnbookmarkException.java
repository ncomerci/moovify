package ar.edu.itba.paw.interfaces.services.exceptions;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;

public class IllegalPostUnbookmarkException extends CustomException {

    public IllegalPostUnbookmarkException() {
        super("error.illegalPostUnbookmarkException", 400);
    }
}
