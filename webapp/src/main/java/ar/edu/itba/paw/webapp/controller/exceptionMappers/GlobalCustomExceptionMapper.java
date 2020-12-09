package ar.edu.itba.paw.webapp.controller.exceptionMappers;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;
import ar.edu.itba.paw.webapp.dto.error.GenericErrorDto;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Singleton
@Component
@Provider
public class GlobalCustomExceptionMapper implements ExceptionMapper<CustomException> {


    @Override
    public Response toResponse(CustomException exception) {
        return Response.status(exception.getResponseStatus()).entity(new GenericErrorDto(exception.getMessage())).build();
    }
}
