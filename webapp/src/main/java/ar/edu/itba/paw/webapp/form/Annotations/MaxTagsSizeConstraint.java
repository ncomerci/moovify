package ar.edu.itba.paw.webapp.form.Annotations;

import ar.edu.itba.paw.webapp.form.Constraints.MaxTagsSizeConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = MaxTagsSizeConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxTagsSizeConstraint {

    String message() default "{javax.validation.constraints.MaxTagsSizeConstraint.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
