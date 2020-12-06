package ar.edu.itba.paw.webapp.dto.error;

public class BeanValidationErrorDto {

    private String attribute;
    private String value;
    private String message;

    public BeanValidationErrorDto() {
        //For Jersey - Do not use
    }

    public BeanValidationErrorDto(String attribute, String value, String message) {
        this.attribute = attribute;
        this.value = value;
        this.message = message;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
