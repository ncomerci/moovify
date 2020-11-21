package ar.edu.itba.paw.webapp.form.constraints;

import ar.edu.itba.paw.webapp.form.annotations.TagSize;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class TagSizeConstraintValidator implements ConstraintValidator<TagSize, Set<String>> {

    private int max;
    @Override
    public void initialize(TagSize constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Set<String> value, ConstraintValidatorContext context) {

        for (String tag: value) {
            if(tag.length() > max)
                return false;
        }
        return true;
    }
}
