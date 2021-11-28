package pl.kmiecik.ais;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.kmiecik.ais.model.TrackService;

import java.util.Collections;

@Controller
public class MapController {

    private final TrackService trackService;

    public MapController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping
    public String getMap(Model model) {
      model.addAttribute("tracks", trackService.getTracks());
      //  model.addAttribute("tracks", Collections.emptyList());
        return "map";
    }

}
