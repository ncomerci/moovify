package ar.edu.itba.paw.webapp.form.annotations;


import ar.edu.itba.paw.webapp.form.constraints.MatchingPasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MatchingPasswordValidator.class)
public @interface PasswordsEqualConstraint {
    String message() default "{javax.validation.constraints.PasswordsEqualConstraint.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


