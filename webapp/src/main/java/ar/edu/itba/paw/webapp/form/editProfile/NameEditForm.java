package ar.edu.itba.paw.webapp.form.editProfile;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class NameEditForm {

    @Size(min = 2, max = 50)
    @Pattern(regexp = "[a-zA-Z]+")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
