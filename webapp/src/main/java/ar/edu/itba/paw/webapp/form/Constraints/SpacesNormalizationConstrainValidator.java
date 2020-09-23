package ar.edu.itba.paw.webapp.form.Constraints;

import ar.edu.itba.paw.webapp.form.Annotations.SpacesNormalization;

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
