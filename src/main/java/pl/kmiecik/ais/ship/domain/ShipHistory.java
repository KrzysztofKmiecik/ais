package pl.kmiecik.ais.ship.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ships_history")
public
class ShipHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "pos_y")
    private double y;
    @Column(name = "pos_x")
    private double x;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ship_id")
    @JsonManagedReference
    private ShipEntity shipEntity;
}
