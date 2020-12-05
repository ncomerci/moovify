package ar.edu.itba.paw.webapp.dto.input.validation.constraints;

import ar.edu.itba.paw.webapp.dto.input.MatchingPasswordForm;
import ar.edu.itba.paw.webapp.dto.input.validation.annotations.MatchingPasswords;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MatchingPasswordsConstraintValidator implements
        ConstraintValidator<MatchingPasswords, Object> {

    @Override
    public void initialize(MatchingPasswords arg0) {
    }

    @Override
    public boolean isValid(Object candidate, ConstraintValidatorContext arg1) {
        MatchingPasswordForm form = (MatchingPasswordForm) candidate;
        return form.getPassword().equals(form.getRepeatPassword());
    }
}