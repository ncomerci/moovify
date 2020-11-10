package ar.edu.itba.paw.webapp.form.editProfile;

import ar.edu.itba.paw.webapp.form.MatchingPasswordForm;
import ar.edu.itba.paw.webapp.form.annotations.MatchingPasswordsConstraint;
import ar.edu.itba.paw.webapp.form.annotations.ValidPassword;

@MatchingPasswordsConstraint()
public class ChangePasswordForm implements MatchingPasswordForm {

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
