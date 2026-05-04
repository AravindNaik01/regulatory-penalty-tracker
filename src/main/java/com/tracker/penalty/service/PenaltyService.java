package com.tracker.penalty.service;

import com.tracker.penalty.dto.PenaltyDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PenaltyService {
    PenaltyDto createPenalty(PenaltyDto penaltyDto);
    Page<PenaltyDto> getAllPenalties(Pageable pageable);
    PenaltyDto getPenaltyById(Long id);
    PenaltyDto updatePenalty(Long id, PenaltyDto penaltyDto);
    void deletePenalty(Long id);
}
