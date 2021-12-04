package pl.kmiecik.ais.ship.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kmiecik.ais.positionAPI.application.port.PositionService;
import pl.kmiecik.ais.ship.application.port.ShipService;
import pl.kmiecik.ais.ship.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipServiceUseCase implements ShipService {

    private final ShipRepository shipRepository;
    private final PositionService positionService;
    private final PositionCoordinateRepository positionCoordinateRepository;

    @Override
    public List<Ship> getShips() {
        List<ShipEntity> shipEntityList = shipRepository.findAll();
        return shipEntityList.stream()
                .map(shipEntity -> this.mapToShip(shipEntity))
                .collect(Collectors.toList());
    }


    @Override
    public void saveShip(Ship ship) {
        ShipEntity shipEntity = mapToEntity(ship);
        shipRepository.save(shipEntity);
    }

    @Override
    public List<Ship> updateShipPosition() {
       return   positionService.getPositions();



    }


    @Override
    public void saveShips(List<Ship> ships) {
        ships.forEach(ship -> updateShipCoordinates(ship));
    }

    @Override
    public void updateShipCoordinates(Ship ship) {
        PositionCoordinate lastCoordinates=new PositionCoordinate();
        if (ship.getName() == null) {

        } else {

            Optional<ShipEntity> currentShip = shipRepository.findByName(ship.getName());

            if (currentShip.isPresent()) {
                Optional<PositionCoordinate> positionCoordinateRepositoryById = positionCoordinateRepository.findById(currentShip.get().getId());
                if(positionCoordinateRepositoryById.isPresent()){
                    positionCoordinateRepositoryById.get().setY(currentShip.get().getY());
                    positionCoordinateRepositoryById.get().setX(currentShip.get().getX());
                    positionCoordinateRepository.save(positionCoordinateRepositoryById.get());
                }else {
                    lastCoordinates.setY(currentShip.get().getY());
                    lastCoordinates.setX(currentShip.get().getX());
                    positionCoordinateRepository.save(lastCoordinates);
                }

                currentShip.get().setX(ship.getX());
                currentShip.get().setY(ship.getY());
                currentShip.get().setDestinationX(ship.getDestinationX());
                currentShip.get().setDestinationY(ship.getDestinationY());
                currentShip.get().setVisibilityInKm(ship.getVisibilityInKm());

                shipRepository.save(currentShip.get());
        }else{

                lastCoordinates.setY(ship.getY());
                lastCoordinates.setX(ship.getX());
                ShipEntity shipEntity=new ShipEntity(ship.getY(), ship.getX(), ship.getName(), ship.getShipStatus(),ship.getDestinationY(),ship.getDestinationX(),ship.getVisibilityInKm(),lastCoordinates);
                positionCoordinateRepository.save(lastCoordinates);
                shipRepository.save(shipEntity);
            }
        }
    }

    @Override
    public void updateShipStatus(Ship ship) {

        if (ship.getName() == null) {

        } else {

            Optional<ShipEntity> currentShip = shipRepository.findByName(ship.getName());
            Long id = currentShip.get().getId();

            if (!currentShip.isPresent()) {
                saveShip(ship);
            } else {
                currentShip.get().setShipStatus(ship.getShipStatus());
                shipRepository.save(currentShip.get());
            }
        }
    }
}
