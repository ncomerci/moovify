package ar.edu.itba.paw.webapp.dto.error;

public class BeanValidationErrorDto {

    private String attribute;
    private String message;

    public BeanValidationErrorDto() {
        // For Jersey - Do not use
    }

    public BeanValidationErrorDto(String attribute, String message) {
        this.attribute = attribute;
        this.message = message;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
