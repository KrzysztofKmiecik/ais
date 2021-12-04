package pl.kmiecik.ais.ship.infrastructure;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kmiecik.ais.ship.domain.ShipHistory;


@Repository
public interface ShipHistoryRepository extends JpaRepository<ShipHistory,Long> {
}
