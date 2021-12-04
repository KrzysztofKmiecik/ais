package pl.kmiecik.ais.ship.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ships")
public class ShipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToMany(mappedBy="shipEntity", cascade=CascadeType.ALL)
    @JsonManagedReference
    private List<ShipHistory> positionHistory;

}
