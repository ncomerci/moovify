package ar.edu.itba.paw.webapp.form.annotations;


import ar.edu.itba.paw.webapp.form.constraints.MatchingPasswordsConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MatchingPasswordsConstraintValidator.class)

public @interface MatchingPasswords {
    String message() default "{javax.validation.constraints.MatchingPasswords.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


