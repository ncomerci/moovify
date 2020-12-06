package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.webapp.dto.input.validation.annotations.SpacesNormalization;
import ar.edu.itba.paw.webapp.dto.input.validation.annotations.ValidPassword;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserEditDto {

    @Size(min = 6, max = 50)
    @Pattern(regexp = "[a-zA-Z0-9#_]+")
    private String username;

    @ValidPassword
    @Size(min = 12, max = 30)
    private String password;

    @Size(min = 2, max = 50)
    @Pattern(regexp = "[a-zA-Z ]+")
    @SpacesNormalization
    private String name;

    @Size(max = 400)
    private String description;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
