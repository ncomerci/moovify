package ar.edu.itba.paw.webapp.form.constraints;

import ar.edu.itba.paw.webapp.form.MatchingPasswordForm;
import ar.edu.itba.paw.webapp.form.annotations.MatchingPasswordsConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MatchingPasswordValidator implements
        ConstraintValidator<MatchingPasswordsConstraint, Object> {

    @Override
    public void initialize(MatchingPasswordsConstraint arg0) {
    }

    @Override
    public boolean isValid(Object candidate, ConstraintValidatorContext arg1) {
        MatchingPasswordForm form = (MatchingPasswordForm) candidate;
        return form.getPassword().equals(form.getRepeatPassword());
    }
}