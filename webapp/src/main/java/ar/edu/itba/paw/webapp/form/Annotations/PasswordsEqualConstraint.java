package ar.edu.itba.paw.webapp.form.Annotations;


import ar.edu.itba.paw.webapp.form.Constraints.PasswordsEqualConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordsEqualConstraintValidator.class)
public @interface PasswordsEqualConstraint {
    String message() ;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


