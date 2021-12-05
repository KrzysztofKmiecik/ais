package pl.kmiecik.ais.email.application;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.kmiecik.ais.email.application.port.EmailService;
import pl.kmiecik.ais.ship.application.port.ShipService;


@Aspect
@Component
class SendEmailAspect {
    private final EmailService emailService;
    private final ShipService shipService;

    @Autowired
    public SendEmailAspect(EmailService emailService, ShipService shipService) {
        this.emailService = emailService;
        this.shipService = shipService;
    }


    @After("@annotation(pl.kmiecik.ais.email.application.SendEmail)")
    private void sendEmailAfter() {
        String message = formatMassage();
        emailService.sendSimpleMessage("Ship's data was changed", message);
    }

    private String formatMassage() {
        return "";
    }
}
