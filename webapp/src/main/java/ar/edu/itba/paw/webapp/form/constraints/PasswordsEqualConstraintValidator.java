package ar.edu.itba.paw.webapp.form.constraints;

import ar.edu.itba.paw.webapp.form.annotations.PasswordsEqualConstraint;
import ar.edu.itba.paw.webapp.form.UserCreateForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsEqualConstraintValidator implements
        ConstraintValidator<PasswordsEqualConstraint, Object> {

    @Override
    public void initialize(PasswordsEqualConstraint arg0) {
    }

    @Override
    public boolean isValid(Object candidate, ConstraintValidatorContext arg1) {
        UserCreateForm user = (UserCreateForm) candidate;
        return user.getPassword().equals(user.getRepeatPassword());
    }
}