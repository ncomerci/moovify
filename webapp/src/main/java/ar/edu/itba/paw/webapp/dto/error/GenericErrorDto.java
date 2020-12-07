package ar.edu.itba.paw.webapp.dto.error;

public class GenericErrorDto {

    private String errorMessage;

    public GenericErrorDto() {
        // For Jersey - Do not Use
    }

    public GenericErrorDto(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
