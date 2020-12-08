package ar.edu.itba.paw.interfaces.exceptions;

public class CustomException extends Exception {

    protected int responseStatus;

    public CustomException() {
        super("Custom exception");
    }

    public CustomException(String message) {
        super(message);
    }

    public int getResponseStatus() {
        return responseStatus;
    }
}
