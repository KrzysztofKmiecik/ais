package pl.kmiecik.ais.ship.application.port;

import pl.kmiecik.ais.ship.domain.ShipDto;
import pl.kmiecik.ais.ship.domain.ShipEntity;

import java.util.List;

public interface ShipService {
    List<ShipDto> getShips();


    void saveShip(ShipDto ship);

    default ShipEntity mapToEntity(ShipDto ship) {
        ShipEntity shipEntity = new ShipEntity(ship.getName(), ship.getShipStatus(), ship.getDestinationY(), ship.getDestinationX(), ship.getVisibilityInKm(),ship.getShipPositionHistoryList())
        return shipEntity;
    }
    default ShipDto mapToShip(ShipEntity shipEntity) {
        ShipDto ship = new ShipDto(shipEntity.getName(),shipEntity.getShipStatus(), shipEntity.getDestinationY(), shipEntity.getDestinationX(), shipEntity.getVisibilityInKm(),shipEntity.getPositionHistory());
        return ship;
    }

    List<ShipDto> updateShipPosition();

    void saveShips(List<ShipDto> ships);

    void updateShipCoordinates(ShipDto ship);

    void updateShipStatus(ShipDto shipDto);
}
