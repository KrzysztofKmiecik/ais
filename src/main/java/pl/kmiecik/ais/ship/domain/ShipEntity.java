package pl.kmiecik.ais.ship.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.websocket.OnError;

@Entity
@Data
@NoArgsConstructor
@Table(name = "ships")
public
class ShipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pos_y")
    private double y;
    @Column(name = "pos_x")
    private double x;
    @Column(name = "name")
    private String name;
    @Column(name="ship_status")
    @Enumerated(value = EnumType.STRING)
    private ShipStatus shipStatus;
    @Column(name = "destination_pos_x")
    private double destinationY;
    @Column(name = "destination_pos_y")
    private double destinationX;
    @Column(name = "visibility_km")
    private int visibilityInKm;

    @OneToOne
    private PositionCoordinate lastPosition;

    public ShipEntity(double y, double x, String name, ShipStatus shipStatus, double destinationY, double destinationX, int visibilityInKm, PositionCoordinate lastPosition) {
        this.y = y;
        this.x = x;
        this.name = name;
        this.shipStatus = shipStatus;
        this.destinationY = destinationY;
        this.destinationX = destinationX;
        this.visibilityInKm = visibilityInKm;
        this.lastPosition = lastPosition;
    }
}
