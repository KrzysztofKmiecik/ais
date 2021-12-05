package pl.kmiecik.ais.ship.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.profiles.active}")
    private String activeProfile;


    /**
     * https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm
     * https://www.freeformatter.com/cron-expression-generator-quartz.html
     * https://crontab.guru
     */
    @GetMapping
   /* @Schedules({@Scheduled(fixedDelay = 1000),
            @Scheduled(cron ="0 * * ? * *" )})*/
    public String getMap(Model model) {
        ;
        System.out.println("Pobranie ");
        List<Ship> ships = shipService.updateShipPosition();
        shipService.saveShips(ships);
        List<Ship> tracks = shipService.getShips();
        /*PositionCoordinate p1=new PositionCoordinate(10.131, 64.711);
        PositionCoordinate p2=new PositionCoordinate(10.132, 64.712);
        PositionCoordinate p3=new PositionCoordinate(10.133, 64.713);

        List<Ship2> tracks = new ArrayList<>();
        Ship2 s1 = new Ship2( 10.131, 64.711,"Ola", ShipStatus.FRIEND, 14, 70, 10,p2);

        tracks.add(s1);*/

        model.addAttribute("tracks", tracks);
        return "map";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String updateShipStatus(final Ship ship, final HttpServletRequest request) {
        shipService.updateShipStatus(ship);
        if (activeProfile.equals("prod")) shipService.sendEmail(ship);
        return "redirect:/map";
    }
}
