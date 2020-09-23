package ar.edu.itba.paw.webapp.form.Constraints;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.webapp.form.Annotations.ValidatedEmail;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidatedEmailConstraintValidator implements ConstraintValidator<ValidatedEmail, String> {

    @Autowired
    private UserService userService;

    public void initialize(ValidatedEmail constraint) {
    }

    public boolean isValid(String obj, ConstraintValidatorContext context) {
        return userService.emailExistsAndIsValidated(obj);
    }
}
