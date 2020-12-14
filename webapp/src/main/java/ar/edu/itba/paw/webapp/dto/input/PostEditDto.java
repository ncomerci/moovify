package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PostEditDto {

    @NotNull
    @Size(min = 1, max = 100000)
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
