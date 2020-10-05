package ar.edu.itba.paw.webapp.form.editProfile;

import ar.edu.itba.paw.webapp.form.Annotations.PasswordsUpdateEqualConstraint;
import ar.edu.itba.paw.webapp.form.Annotations.ValidPassword;

@PasswordsUpdateEqualConstraint(message= "Passwords must match")
public class ChangePasswordForm {

    @ValidPassword
    private String password;

    private String repeatPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
