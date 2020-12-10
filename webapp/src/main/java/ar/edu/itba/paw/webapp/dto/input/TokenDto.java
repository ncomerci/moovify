package ar.edu.itba.paw.webapp.dto.input;

import org.hibernate.validator.constraints.NotEmpty;

public class TokenDto {

    @NotEmpty
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
