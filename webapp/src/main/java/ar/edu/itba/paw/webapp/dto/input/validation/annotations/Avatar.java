package ar.edu.itba.paw.webapp.dto.input.validation.annotations;

import ar.edu.itba.paw.webapp.dto.input.validation.constraints.AvatarConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = AvatarConstraintValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface Avatar {

    String message() default "{javax.validation.constraints.Avatar.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
