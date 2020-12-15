package ar.edu.itba.paw.webapp.config;

import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Provider
public class AcceptHeaderLocaleFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final List<Locale> acceptableLanguages = requestContext.getAcceptableLanguages();

        if(!acceptableLanguages.isEmpty())
            LocaleContextHolder.setLocale(acceptableLanguages.get(0));
    }
}
