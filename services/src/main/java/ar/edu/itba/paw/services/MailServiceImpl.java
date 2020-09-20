package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender emailSender;

    private static final String FROM = "Moovify";

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {

        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(to);
        email.setFrom(FROM);
        email.setSentDate(Timestamp.valueOf(LocalDateTime.now()));
        email.setText(text);

        emailSender.send(email);
    }

}
