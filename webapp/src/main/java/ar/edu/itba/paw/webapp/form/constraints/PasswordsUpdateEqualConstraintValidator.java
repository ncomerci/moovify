package ar.edu.itba.paw.webapp.form.constraints;

import ar.edu.itba.paw.webapp.form.annotations.PasswordsUpdateEqualConstraint;
import ar.edu.itba.paw.webapp.form.editProfile.ChangePasswordForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsUpdateEqualConstraintValidator implements
        ConstraintValidator<PasswordsUpdateEqualConstraint, Object> {
    @Override
    public void initialize(PasswordsUpdateEqualConstraint passwordsUpdateEqualConstraint) {

    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        ChangePasswordForm user = (ChangePasswordForm) o;
        return user.getPassword().equals(user.getRepeatPassword());
    }
}
