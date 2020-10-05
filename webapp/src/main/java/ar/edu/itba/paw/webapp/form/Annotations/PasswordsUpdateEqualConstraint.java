package ar.edu.itba.paw.webapp.form.Annotations;

import ar.edu.itba.paw.webapp.form.Constraints.PasswordsEqualConstraintValidator;
import ar.edu.itba.paw.webapp.form.Constraints.PasswordsUpdateEqualConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordsUpdateEqualConstraintValidator.class)
public @interface PasswordsUpdateEqualConstraint {

    String message() ;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
