package pl.kmiecik.ais.ship.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class Ship2 {

    private double y;
    private double x;
    private String name;
    private ShipStatus shipStatus;
    private double destinationY;
    private double destinationX;
    private int visibilityInKm;
    private PositionCoordinate lastPosition;


}
