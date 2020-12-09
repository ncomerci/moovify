package ar.edu.itba.paw.webapp.dto.generic;

public class GenericBooleanResponseDto {

    private boolean response;

    public GenericBooleanResponseDto() {
        // For Jersey - Do not use
    }

    public GenericBooleanResponseDto(boolean response) {
        this.response = response;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }
}
