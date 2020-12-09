package ar.edu.itba.paw.webapp.controller.exceptionMappers;

import ar.edu.itba.paw.interfaces.exceptions.CustomException;
import ar.edu.itba.paw.webapp.dto.error.GenericErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Locale;

@Singleton
@Component
@Provider
public class GlobalCustomExceptionMapper implements ExceptionMapper<CustomException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalCustomExceptionMapper.class);

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(CustomException exception) {

        final String message = messageSource.getMessage(exception.getMessageCode(), null, LocaleContextHolder.getLocale());

        final String messageForLogger = messageSource.getMessage(exception.getMessageCode(), null, Locale.ENGLISH);

        LOGGER.error("{} was thrown with message {}. Responding with Http Status {}", exception.getClass().getName(), messageForLogger, exception.getResponseStatus());

        return Response.status(exception.getResponseStatus()).entity(new GenericErrorDto(message)).build();
    }
}
