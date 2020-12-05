package ar.edu.itba.paw.webapp.form.editProfile;

import ar.edu.itba.paw.webapp.dto.input.MatchingPasswordForm;
import ar.edu.itba.paw.webapp.dto.input.validation.annotations.MatchingPasswords;
import ar.edu.itba.paw.webapp.dto.input.validation.annotations.ValidPassword;

import javax.validation.constraints.Size;

@MatchingPasswords()
public class ChangePasswordForm implements MatchingPasswordForm {

    @ValidPassword
    @Size(min = 12, max = 30)
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
