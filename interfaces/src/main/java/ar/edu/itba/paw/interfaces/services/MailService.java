package ar.edu.itba.paw.interfaces.services;

public interface MailService {

    void sendSimpleEmail(String to, String subject, String text);
}
