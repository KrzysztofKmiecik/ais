package pl.kmiecik.ais.ship.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ShipDto {

    private String name;
    private ShipStatus shipStatus;
    private double destinationY;
    private double destinationX;
    private int visibilityInKm;
    private List<Point> points;


}
