package ar.edu.itba.paw.webapp.form.annotations;

import ar.edu.itba.paw.webapp.form.constraints.MoviesSizeConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = MoviesSizeConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MoviesSizeConstraint {

    String message() default "{javax.validation.constraints.MoviesSizeConstraint.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
