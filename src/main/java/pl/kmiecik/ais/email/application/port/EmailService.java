package pl.kmiecik.ais.email.application.port;

public interface EmailService {
    void sendSimpleMessage(
            String to, String subject, String text);
}
