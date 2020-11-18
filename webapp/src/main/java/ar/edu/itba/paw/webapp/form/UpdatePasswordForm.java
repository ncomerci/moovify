package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.annotations.ValidPassword;

import javax.validation.constraints.Size;

public class UpdatePasswordForm {

    @ValidPassword
    @Size(min = 12, max = 30)
    private String password;

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
