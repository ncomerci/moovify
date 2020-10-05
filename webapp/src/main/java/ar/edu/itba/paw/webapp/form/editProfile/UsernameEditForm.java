package ar.edu.itba.paw.webapp.form.editProfile;

import ar.edu.itba.paw.webapp.form.Annotations.UniqueUsername;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UsernameEditForm {

    @Size(min = 6, max = 50)
    @UniqueUsername
    @Pattern(regexp = "[a-zA-Z0-9#_]+")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
