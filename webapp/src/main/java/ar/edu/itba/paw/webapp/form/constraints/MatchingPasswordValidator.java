package ar.edu.itba.paw.webapp.form.constraints;

import ar.edu.itba.paw.webapp.form.MatchingPasswordForm;
import ar.edu.itba.paw.webapp.form.annotations.PasswordsEqualConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MatchingPasswordValidator implements
        ConstraintValidator<PasswordsEqualConstraint, Object> {

    @Override
    public void initialize(PasswordsEqualConstraint arg0) {
    }

    @Override
    public boolean isValid(Object candidate, ConstraintValidatorContext arg1) {
        MatchingPasswordForm form = (MatchingPasswordForm) candidate;
        return form.getPassword().equals(form.getRepeatPassword());
    }
}