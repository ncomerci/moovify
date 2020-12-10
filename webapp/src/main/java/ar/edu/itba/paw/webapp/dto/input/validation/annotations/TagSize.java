package ar.edu.itba.paw.webapp.dto.input.validation.annotations;



import ar.edu.itba.paw.webapp.dto.input.validation.constraints.TagSizeConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = TagSizeConstraintValidator.class)
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface TagSize {

    String message() default "{custom.validation.TagsSize.message}" ;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int max();

}
