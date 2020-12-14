package ar.edu.itba.paw.webapp.dto.input;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PasswordResetDto {

    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9#_]+")
    @Size(min = 12, max = 30)
    private String password;

    @NotEmpty
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
