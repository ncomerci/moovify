package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.webapp.dto.input.validation.annotations.SpacesNormalization;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CommentCreateDto {

    @NotNull
    @Size(min = 1, max = 400)
    @SpacesNormalization
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
