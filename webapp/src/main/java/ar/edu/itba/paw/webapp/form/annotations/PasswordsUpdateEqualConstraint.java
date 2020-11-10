package ar.edu.itba.paw.webapp.form.annotations;

import ar.edu.itba.paw.webapp.form.constraints.PasswordsUpdateEqualConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordsUpdateEqualConstraintValidator.class)
public @interface PasswordsUpdateEqualConstraint {

    String message() default "{javax.validation.constraints.PasswordsUpdateEqualConstraintValidator.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
