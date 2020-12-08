package ar.edu.itba.paw.webapp.dto.generic;

public class GenericIntegerValueDto {

    private int value;

    public GenericIntegerValueDto() {
        // For Jersey - Do not use
    }

    public GenericIntegerValueDto(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
