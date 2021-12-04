package pl.kmiecik.ais.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.kmiecik.ais.ship.application.port.ShipService;

@Component
@RequiredArgsConstructor
class init {

    private final ShipService shipService;

    @EventListener(ApplicationReadyEvent.class)
    public void updateDB() {
        shipService.updateShipPosition();
    }
}
