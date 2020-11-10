package ar.edu.itba.paw.webapp.form.annotations;

import ar.edu.itba.paw.webapp.form.constraints.SpacesNormalizationConstrainValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = SpacesNormalizationConstrainValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface SpacesNormalization {

    String message() default "{javax.validation.constraints.SpacesNormalization.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
