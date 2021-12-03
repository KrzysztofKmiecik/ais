package pl.kmiecik.ais;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.kmiecik.ais.ship.domain.Point;
import pl.kmiecik.ais.ship.application.port.TrackService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MapController {

    private final TrackService trackService;




    @GetMapping
    public String getMap(Model model) {

        List<Point> tracks = trackService.getPoints();
        trackService.saveShips(tracks);
        model.addAttribute("tracks", tracks);
      //  model.addAttribute("tracks", Collections.emptyList());
        return "map";
    }

}
