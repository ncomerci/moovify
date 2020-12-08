package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.CustomRuntimeException;
import ar.edu.itba.paw.webapp.dto.error.GenericErrorDto;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Singleton
@Component
@Provider
public class GlobalCustomRuntimeExceptionMapper implements ExceptionMapper<CustomRuntimeException> {

    @Override
    public Response toResponse(CustomRuntimeException exception) {
        return Response.status(exception.getResponseStatus()).entity(new GenericErrorDto(exception.getMessage())).build();
    }
}
