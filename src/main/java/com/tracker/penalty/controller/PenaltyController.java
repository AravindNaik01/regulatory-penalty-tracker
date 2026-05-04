package com.tracker.penalty.controller;

import com.tracker.penalty.dto.PenaltyDto;
import com.tracker.penalty.service.PenaltyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/penalties")
public class PenaltyController {

    private final PenaltyService penaltyService;

    public PenaltyController(PenaltyService penaltyService) {
        this.penaltyService = penaltyService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<PenaltyDto>> getAllPenalties(Pageable pageable) {
        Page<PenaltyDto> penalties = penaltyService.getAllPenalties(pageable);
        return ResponseEntity.ok(penalties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PenaltyDto> getPenaltyById(@PathVariable Long id) {
        PenaltyDto penalty = penaltyService.getPenaltyById(id);
        return ResponseEntity.ok(penalty);
    }

    @PostMapping("/create")
    public ResponseEntity<PenaltyDto> createPenalty(@Valid @RequestBody PenaltyDto penaltyDto) {
        PenaltyDto createdPenalty = penaltyService.createPenalty(penaltyDto);
        return new ResponseEntity<>(createdPenalty, HttpStatus.CREATED);
    }
}
