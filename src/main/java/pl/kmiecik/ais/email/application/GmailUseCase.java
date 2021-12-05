package pl.kmiecik.ais.email.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.kmiecik.ais.email.application.port.EmailService;


/**
 * https://www.baeldung.com/spring-email
 */

@Service
@RequiredArgsConstructor
public class GmailUseCase implements EmailService {

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.sendTo}")
    private String toEmail;

    @Override
    public void sendSimpleMessage(final String subject, final String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
