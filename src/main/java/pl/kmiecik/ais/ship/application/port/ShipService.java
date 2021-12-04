package pl.kmiecik.ais.ship.application.port;

import pl.kmiecik.ais.ship.domain.Ship;
import pl.kmiecik.ais.ship.domain.ShipEntity;

import java.util.List;

public interface ShipService {
    List<Ship> getShips();


    void saveShip(Ship ship);

    default ShipEntity mapToEntity(Ship ship) {
        ShipEntity shipEntity = new ShipEntity(ship.getY(), ship.getX(), ship.getName(),ship.getShipStatus(), ship.getDestinationY(), ship.getDestinationX(), ship.getVisibilityInKm());
        return shipEntity;
    }
    default Ship mapToShip(ShipEntity shipEntity) {
        Ship ship = new Ship(shipEntity.getY(), shipEntity.getX(), shipEntity.getName(),shipEntity.getShipStatus(), shipEntity.getDestinationY(), shipEntity.getDestinationX(), shipEntity.getVisibilityInKm());
        return ship;
    }

    List<Ship> updateShipPosition();

    void saveShips(List<Ship> ships);

    void updateShipCoordinates(Ship ship);

    void updateShipStatus(Ship ship);
}
