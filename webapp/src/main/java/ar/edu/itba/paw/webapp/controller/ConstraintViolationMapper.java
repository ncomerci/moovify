package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.error.BeanValidationErrorDto;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Singleton
@Component
@Provider
public class ConstraintViolationMapper implements ExceptionMapper<ConstraintViolationException> {

    /*
    *  https://stackoverflow.com/questions/43423036/custom-validationerror-class-in-jersey-to-send-only-string-message-of-error
    */

    @Override
    public Response toResponse(ConstraintViolationException e) {

        // There can be multiple constraint Violations
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();

        ArrayList<BeanValidationErrorDto> errors = new ArrayList<>();

        for (ConstraintViolation<?> violation : violations) {
            errors.add(
                    new BeanValidationErrorDto(
                            getViolationPropertyName(violation),
                            violation.getInvalidValue().toString(),
                            violation.getMessage()
                    )
            );
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new GenericEntity<Collection<BeanValidationErrorDto>>(errors) {}).build();
    }

    private String getViolationPropertyName(ConstraintViolation<?> violation) {

        final String propertyPath = violation.getPropertyPath().toString();

        return propertyPath.substring(propertyPath.lastIndexOf(".") + 1);
    }

}