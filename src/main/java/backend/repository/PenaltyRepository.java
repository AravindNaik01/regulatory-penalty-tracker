package backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import backend.entity.Penalty;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
}