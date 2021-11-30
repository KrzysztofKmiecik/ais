package pl.kmiecik.ais.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Point {

    private double y;
    private double x;
    private String name;
    private double destinationY;
    private double destinationX;
    private int visibilityInKm;


}
