package ar.edu.itba.paw.webapp.form.editProfile;

import javax.validation.constraints.Size;

public class DescriptionEditForm {

    @Size(max=400)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
