package pl.kmiecik.ais.email.application.port;

public interface EmailService {
    void sendSimpleMessage( String subject, String text);
}
