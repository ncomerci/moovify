package ar.edu.itba.paw.webapp.config;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// TODO: Ver si lo puedo hacer sin tocar el web.xml mediante interfaces de Jersey
@Component
public class AcceptHeaderLocaleFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        LocaleContextHolder.setLocale(request.getLocale());

        filterChain.doFilter(request, response);
    }
}
