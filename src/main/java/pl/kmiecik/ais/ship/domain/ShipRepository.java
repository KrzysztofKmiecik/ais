package pl.kmiecik.ais.ship.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipRepository extends JpaRepository<ShipEntity, Long> {
}
