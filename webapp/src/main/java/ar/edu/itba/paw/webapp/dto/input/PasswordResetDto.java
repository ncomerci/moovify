package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.webapp.dto.input.validation.annotations.ValidPassword;

import javax.validation.constraints.Size;

public class PasswordResetDto {

    @ValidPassword
    @Size(min = 12, max = 30)
    private String password;

    private String token;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
