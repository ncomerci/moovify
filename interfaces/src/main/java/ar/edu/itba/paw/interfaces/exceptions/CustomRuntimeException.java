package ar.edu.itba.paw.interfaces.exceptions;

public abstract class CustomRuntimeException extends RuntimeException {

    private final int responseStatus;
    private final String messageCode;

    public CustomRuntimeException(String messageCode, int responseStatus) {
        super();
        this.messageCode = messageCode;
        this.responseStatus = responseStatus;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
