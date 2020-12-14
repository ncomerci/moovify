package ar.edu.itba.paw.webapp.dto.input.validation.constraints;

import ar.edu.itba.paw.webapp.dto.input.validation.annotations.SpacesNormalization;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SpacesNormalizationConstrainValidator implements ConstraintValidator<SpacesNormalization, String> {
    @Override
    public void initialize(SpacesNormalization spacesNormalization) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return  s == null || !s.replaceAll("[ \t\r\n]+", "").equals("");
    }
}
