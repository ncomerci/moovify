package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

@Service
public class MailServiceImpl implements MailService {

    private static final String EMAIL_ENCODING = StandardCharsets.UTF_8.displayName();

    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    @Qualifier("applicationBasePath")
    private String applicationBasePath;

    private static final String MOOVIFY_EMAIL_ADDRESS = "moovifyCo@gmail.com";

    @Async
    @Override
    public void sendEmail(String destination, String subject, String template, Map<String, Object> variables, Locale locale) {

        // Prepare the evaluation context
        final Context context = new Context(locale);

        context.setVariable("applicationBasePath", applicationBasePath);

        if(variables != null)
            context.setVariables(variables);

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = emailSender.createMimeMessage();

        try {
            final MimeMessageHelper message =
                    new MimeMessageHelper(mimeMessage, true, EMAIL_ENCODING);

            message.setSubject(subject);
            message.setFrom(MOOVIFY_EMAIL_ADDRESS);
            message.setTo(destination);

            message.setText(templateEngine.process(template, context), true);

            emailSender.send(mimeMessage);

            LOGGER.info("Email sent successfully. Subject {}; Destination {}; Template {}; Variables {}", subject, destination, template, variables);
        }
        catch(MessagingException e) {
            LOGGER.error("Email sending failed. Subject {}; Destination {}; Template {}; Variables {}", subject, destination, template, variables, e);
            throw new RuntimeException();
        }
    }

}
