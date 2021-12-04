package pl.kmiecik.ais.ship.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kmiecik.ais.positionAPI.application.port.PositionService;
import pl.kmiecik.ais.ship.application.port.ShipService;
import pl.kmiecik.ais.ship.domain.ShipDto;
import pl.kmiecik.ais.ship.domain.ShipEntity;
import pl.kmiecik.ais.ship.domain.ShipHistory;
import pl.kmiecik.ais.ship.infrastructure.ShipHistoryRepository;
import pl.kmiecik.ais.ship.infrastructure.ShipRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipServiceUseCase implements ShipService {

    private final ShipRepository shipRepository;
    private final ShipHistoryRepository shipHistoryRepository;
    private final PositionService positionService;

    @Override
    public List<ShipDto> getShips() {
        List<ShipEntity> shipEntityList = shipRepository.findAll();
        return shipEntityList.stream()
                .map(shipEntity -> this.mapToShip(shipEntity))
                .collect(Collectors.toList());
    }


    @Override
    public void saveShip(ShipDto ship) {
        ShipEntity shipEntity = new ShipEntity();
        ShipHistory shipHistory = new ShipHistory();
        shipEntity.setShipStatus(ship.getShipStatus());
        shipEntity.setName(ship.getName());
        shipEntity.setDestinationX(ship.getDestinationX());
        shipEntity.setDestinationY(ship.getDestinationY());

        shipHistory.setX(ship.getPoints().get(0).getX());
        shipHistory.setY(ship.getPoints().get(0).getY());
        shipHistory.setShipEntity(shipEntity);

        shipRepository.save(shipEntity);
        shipHistoryRepository.save(shipHistory);
    }

    @Override
    public List<ShipEntity> updateShipPosition() {
        List<ShipEntity> newPositions = positionService.getPositions();

        return newPositions.stream()
                .map(ship -> {
                    Optional<ShipEntity> currentShip = shipRepository.findByName(ship.getName());
                    if (currentShip.isPresent()) {
                        ship.setShipStatus(currentShip.get().getShipStatus());
                    }
                    return ship;
                })
                .collect(Collectors.toList());

    }


    @Override
    public void saveShips(List<ShipDto> ships) {
        ships.forEach(ship -> updateShipCoordinates(ship));
    }

    @Override
    public void updateShipCoordinates(ShipDto ship) {

        if (ship.getName() == null) {

        } else {

            Optional<ShipEntity> currentShip = shipRepository.findByName(ship.getName());

            if (!currentShip.isPresent()) {
                saveShip(ship);
            } else {
                ShipHistory shipHistory=new ShipHistory();

                currentShip.get().setDestinationX(ship.getDestinationX());
                currentShip.get().setDestinationY(ship.getDestinationY());
                currentShip.get().setVisibilityInKm(ship.getVisibilityInKm());

                shipHistory.setShipEntity(currentShip.get());
                shipHistory.setX(ship.getPoints().get(0).getX());
                shipHistory.setY(ship.getPoints().get(0).getY());
                shipRepository.save(currentShip.get());
                shipHistoryRepository.save(shipHistory);
            }
        }
    }

    @Override
    public void updateShipStatus(ShipDto shipDto) {

        if (shipDto.getName() == null) {

        } else {

            Optional<ShipEntity> currentShip = shipRepository.findByName(shipDto.getName());
            Long id = currentShip.get().getId();

            if (!currentShip.isPresent()) {
                saveShip(shipDto);
            } else {
                currentShip.get().setShipStatus(shipDto.getShipStatus());
                shipRepository.save(currentShip.get());
            }
        }
    }
}
