package ar.edu.itba.paw.webapp.form.Annotations;

import ar.edu.itba.paw.webapp.form.Constraints.UniqueEmailConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = UniqueEmailConstraintValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface UniqueEmail {

    String message() default "{javax.validation.constraints.UniqueEmail.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
