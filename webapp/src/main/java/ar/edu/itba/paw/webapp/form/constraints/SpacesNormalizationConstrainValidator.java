package ar.edu.itba.paw.webapp.form.constraints;

import ar.edu.itba.paw.webapp.form.annotations.SpacesNormalization;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SpacesNormalizationConstrainValidator implements ConstraintValidator<SpacesNormalization, String> {
    @Override
    public void initialize(SpacesNormalization spacesNormalization) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return  !s.replaceAll("[ \t\r\n]+", "").equals("");
    }
}
