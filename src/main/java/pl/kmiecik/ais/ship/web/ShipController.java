package pl.kmiecik.ais.ship.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.kmiecik.ais.ship.application.port.ShipService;
import pl.kmiecik.ais.ship.domain.Ship;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ShipController {

    private final ShipService shipService;


    @GetMapping
    public String getMap(Model model) {

        List<Ship> tracks = shipService.getShips();
        shipService.saveShips(tracks);
        model.addAttribute("tracks", tracks);
        return "map";
    }

}
