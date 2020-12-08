package ar.edu.itba.paw.webapp.dto.generic;

public class GenericIntegerValueDto {

    private int value;

    public GenericIntegerValueDto() {
        // For Jersey - Do not use
    }

    public GenericIntegerValueDto(int response) {
        this.value = response;
    }

    public int getResponse() {
        return value;
    }

    public void setResponse(int response) {
        this.value = response;
    }
}
