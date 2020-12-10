package ar.edu.itba.paw.webapp.controller.exceptionMappers;

import ar.edu.itba.paw.webapp.dto.error.GenericErrorDto;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Singleton
@Component
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {


    @Override
    public Response toResponse(Exception exception) {

        final int status;

        if(exception instanceof WebApplicationException)
            status = ((WebApplicationException) exception).getResponse().getStatus();

        else
            status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

        return Response.status(status).entity(new GenericErrorDto(exception.getMessage())).build();
    }
}