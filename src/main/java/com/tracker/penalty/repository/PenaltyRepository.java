package com.tracker.penalty.repository;

import com.tracker.penalty.entity.Penalty;
import com.tracker.penalty.entity.PenaltyStatus;
import com.tracker.penalty.entity.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    List<Penalty> findByStatus(PenaltyStatus status);

    List<Penalty> findBySeverity(Severity severity);

    @Query("SELECT p FROM Penalty p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Penalty> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Penalty p WHERE " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:severity IS NULL OR p.severity = :severity)")
    List<Penalty> filterPenalties(@Param("status") PenaltyStatus status, @Param("severity") Severity severity);
}
