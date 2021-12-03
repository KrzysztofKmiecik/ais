package pl.kmiecik.ais.ship.application.port;

import pl.kmiecik.ais.positionAPI.domain.Datum;
import pl.kmiecik.ais.ship.domain.Ship;
import pl.kmiecik.ais.ship.domain.ShipEntity;

import java.util.List;

public interface ShipService {
    List<Ship> getShips();

    Datum getDestination(String destinationName, List<Double> coordinates);

    void saveShip(Ship ship);

    default ShipEntity mapToEntity(Ship ship) {
        ShipEntity shipEntity = new ShipEntity(ship.getY(), ship.getX(), ship.getName(),ship.getShipStatus(), ship.getDestinationY(), ship.getDestinationX(), ship.getVisibilityInKm());
        return shipEntity;
    }

    void saveShips(List<Ship> ships);
}
