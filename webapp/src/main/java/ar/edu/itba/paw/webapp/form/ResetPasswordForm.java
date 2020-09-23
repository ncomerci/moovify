package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.Annotations.ValidatedEmail;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;



public class ResetPasswordForm {

    @Email
    @NotEmpty
    @ValidatedEmail
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
