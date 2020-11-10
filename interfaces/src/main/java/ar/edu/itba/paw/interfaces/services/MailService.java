package ar.edu.itba.paw.interfaces.services;

import java.util.Locale;
import java.util.Map;

public interface MailService {

    void sendEmail(String destination, String subject, String template, Map<String, Object> variables, Locale locale);
}
