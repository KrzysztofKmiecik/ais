package pl.kmiecik.ais.positionAPI.application.port;

import pl.kmiecik.ais.positionAPI.domain.Datum;
import pl.kmiecik.ais.ship.domain.ShipDto;

import java.util.List;

public interface PositionService {
    List<ShipDto> getPositions();
    Datum getDestination(String destinationName, List<Double> coordinates);

}
