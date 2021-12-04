package pl.kmiecik.ais.ship.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.kmiecik.ais.ship.application.port.ShipService;
import pl.kmiecik.ais.ship.domain.Ship;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ShipController {

    private final ShipService shipService;

    /**
     * https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm
     * https://www.freeformatter.com/cron-expression-generator-quartz.html
     * https://crontab.guru
     */
    @GetMapping
   /* @Schedules({@Scheduled(fixedDelay = 1000),
            @Scheduled(cron ="0 * * ? * *" )})*/
    public String getMap(Model model) {
        System.out.println("Pobranie ");
        List<Ship> ships = shipService.updateShipPosition();
        shipService.saveShips(ships);
        List<Ship> tracks = shipService.getShips();
        model.addAttribute("tracks", tracks);
        return "map";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String updateShipStatus(final Ship ship, final HttpServletRequest request) {
        shipService.updateShipStatus(ship);
        return "redirect:/map";
    }


}
