package ar.edu.itba.paw.webapp.form.annotations;

import ar.edu.itba.paw.webapp.form.constraints.PasswordConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ValidPassword {

    String message() default "{javax.validation.constraints.ValidPassword.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
