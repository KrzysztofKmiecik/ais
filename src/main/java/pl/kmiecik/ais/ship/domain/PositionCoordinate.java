package pl.kmiecik.ais.ship.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Entity
@NoArgsConstructor
public class PositionCoordinate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "pos_Y")
    private double y;
    @Column(name = "pos_X")
    private double x;

    public PositionCoordinate(double y, double x) {
        this.y = y;
        this.x = x;
    }
}
