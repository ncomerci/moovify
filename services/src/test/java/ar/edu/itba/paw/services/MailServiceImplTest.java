package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class MailServiceImplTest {

    private static final String DESTINATION = "test@mail.com";
    private static final String SUBJECT = "Test mail";
    private static final String TEMPLATE = "template";
    private static final Map<String, Object> VARIABLES = new HashMap<>();

    @Mock
    private MailService mailService;

    @Test
    public void testSendEmail() {
        // TODO: Como testear correctamente el envio mails.
        mailService.sendEmail(DESTINATION, SUBJECT, TEMPLATE, VARIABLES);
    }
}