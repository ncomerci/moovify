package ar.edu.itba.paw.webapp.form.editProfile;

import ar.edu.itba.paw.webapp.form.annotations.SpacesNormalization;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserEditForm {

    @Size(min = 6, max = 50)
    @Pattern(regexp = "[a-zA-Z0-9#_]+")
    private String username;

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
}
