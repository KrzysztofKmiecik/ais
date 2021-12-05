package pl.kmiecik.ais.ship.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kmiecik.ais.email.application.port.EmailService;
import pl.kmiecik.ais.positionAPI.application.port.PositionService;
import pl.kmiecik.ais.ship.domain.*;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@SpringBootTest
class ShipServiceUseCaseTest {


    private ShipRepository shipRepository = mock(ShipRepository.class);
    private PositionService positionService = mock(PositionService.class);
    private PositionCoordinateRepository positionCoordinateRepository = mock(PositionCoordinateRepository.class);
    private EmailService emailService = mock(EmailService.class);
    private ShipServiceUseCase shipService = new ShipServiceUseCase(shipRepository, positionService, positionCoordinateRepository, emailService);

    @BeforeEach
    private void setupRepository() {
        ShipEntity ship1 = new ShipEntity(10.201827, 63.451793, "LAGATUN", ShipStatus.ENEMY, 10.201827, 63.451793, 38, new PositionCoordinate(10.179672, 63.487595));
        ShipEntity ship2 = new ShipEntity(10.140605, 63.508917, "MUNKEN", ShipStatus.FRIEND, 10.140605, 63.508917, 19, new PositionCoordinate(10.140605, 63.508917));
        ShipEntity ship3 = new ShipEntity(10.392213, 63.434048, "ANNE MARGRETHE", ShipStatus.ENEMY, 10.392213, 63.434048, 4, new PositionCoordinate(10.392213, 63.434048));
        List<ShipEntity> expectedShipList = Arrays.asList(ship1, ship2, ship3);
        doReturn(expectedShipList).when(shipRepository).findAll();
    }

    @Test
    @DisplayName("should Get Ships")
    void shouldGetShips() {
        //given

        //when
        List<Ship> actualShipList = shipService.getShips();
        //then
        Assertions.assertEquals(ShipStatus.FRIEND, actualShipList.get(1).getShipStatus());
        Assertions.assertEquals(ShipStatus.ENEMY, actualShipList.get(0).getShipStatus());
        Assertions.assertEquals(ShipStatus.ENEMY, actualShipList.get(2).getShipStatus());
        Assertions.assertEquals("MUNKEN", actualShipList.get(1).getName());
        Assertions.assertEquals("LAGATUN", actualShipList.get(0).getName());
        Assertions.assertEquals("ANNE MARGRETHE", actualShipList.get(2).getName());
    }


}