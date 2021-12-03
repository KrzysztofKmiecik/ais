package pl.kmiecik.ais.ship.web;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kmiecik.ais.ship.application.port.ShipService;
import pl.kmiecik.ais.ship.domain.Ship;

import java.util.List;

@RestController
@RequestMapping("api/ships")
@AllArgsConstructor
class ShipRestController {
    private final ShipService shipService;

    @GetMapping
    public List<Ship> getAllShips() {
        return shipService.getShips();
    }
}
