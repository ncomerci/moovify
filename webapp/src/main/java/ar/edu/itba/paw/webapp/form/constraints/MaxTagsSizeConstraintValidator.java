package ar.edu.itba.paw.webapp.form.constraints;


import ar.edu.itba.paw.webapp.form.annotations.MaxTagsSizeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class MaxTagsSizeConstraintValidator implements ConstraintValidator<MaxTagsSizeConstraint, Set<String>> {


    @Override
    public void initialize(MaxTagsSizeConstraint maxSizeConstraint) {
    }

    @Override
    public boolean isValid(Set<String> values, ConstraintValidatorContext context) {
        return values == null || values.size() <= 5;
    }
}