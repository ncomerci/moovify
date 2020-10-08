package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.Annotations.Avatar;
import ar.edu.itba.paw.webapp.form.Annotations.PasswordsEqualConstraint;
import ar.edu.itba.paw.webapp.form.Annotations.ValidPassword;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@PasswordsEqualConstraint(message= "Passwords must match")
public class UserCreateForm {

    @Size(min = 6, max = 50)
    @Pattern(regexp = "[a-zA-Z0-9#_]+")
    private String username;

    @ValidPassword
    private String password;

    private String repeatPassword;

    @Pattern(regexp = "[a-zA-Z]+")
    private String name;

    @Email
    @NotEmpty
    private String email;

    @Size(max=400)
    private String description;

    @Avatar
    private MultipartFile avatar;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getAvatar() {
        return avatar;
    }

    public void setAvatar(MultipartFile avatar) {
        this.avatar = avatar;
    }
}
