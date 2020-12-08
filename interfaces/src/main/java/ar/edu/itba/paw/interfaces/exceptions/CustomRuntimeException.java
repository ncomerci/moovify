package ar.edu.itba.paw.interfaces.exceptions;

public class CustomRuntimeException extends RuntimeException {

    protected int responseStatus;

    public CustomRuntimeException() {
        super("Custom runtime exception");
    }

    public CustomRuntimeException(String message) {
        super(message);
    }

    public int getResponseStatus() {
        return responseStatus;
    }
}
