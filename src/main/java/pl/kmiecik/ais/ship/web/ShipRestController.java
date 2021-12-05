package pl.kmiecik.ais.ship.web;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.kmiecik.ais.ship.application.port.ShipService;
import pl.kmiecik.ais.ship.domain.PositionCoordinate;
import pl.kmiecik.ais.ship.domain.Ship;
import pl.kmiecik.ais.ship.domain.ShipStatus;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("api/ships")
@RequiredArgsConstructor
class ShipRestController {
    private final ShipService shipService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @GetMapping
    public List<Ship> getAllShips() {
        return shipService.getShips();
    }


    /**
     * BODY example:
     * {
     * "y": 10.1971,
     * "x": 63.464607,
     * "name": "MUNKEN",
     * "shipStatus": "FRIEND",
     * "destinationY": 10.1971,
     * "destinationX": 63.464607,
     * "visibilityInKm": 16,
     * "lastPosition": {
     * "y": 10.201825,
     * "x": 63.451797,
     * "id": 5
     * }
     * }
     *
     * @param command
     */


    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void setFriendStatus(@Valid @RequestBody RestShipCommand command) {
        Ship ship = command.toShip();
        shipService.updateShipStatus(ship);
        if (activeProfile.equals("prod")) shipService.sendEmail(ship);
    }

    @Data
    @Builder
    private static class RestShipCommand {
        @Min(-90)
        @Max(90)
        private double y;
        @Min(-90)
        @Max(90)
        private double x;
        @NotBlank
        private String name;
        @NotNull
        private ShipStatus shipStatus;
        @Min(-90)
        @Max(90)
        private double destinationY;
        @Min(-90)
        @Max(90)
        private double destinationX;
        @Min(0)
        private int visibilityInKm;
        @NotNull
        private PositionCoordinate lastPosition;

        public Ship toShip() {
            return new Ship(y, x, name, shipStatus, destinationY, destinationX, visibilityInKm, lastPosition);
        }
    }
}
