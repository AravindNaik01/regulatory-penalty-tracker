package com.tracker.controller;

import com.tracker.model.AuditLog;
import com.tracker.model.Penalty;
import com.tracker.repository.AuditLogRepository;
import com.tracker.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/penalties")
@RequiredArgsConstructor
public class PenaltyController {

    private final PenaltyService penaltyService;
    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public ResponseEntity<List<Penalty>> getAllPenalties() {
        return ResponseEntity.ok(penaltyService.getAllPenalties());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Penalty> getPenaltyById(@PathVariable Long id) {
        return ResponseEntity.ok(penaltyService.getPenaltyById(id));
    }

    @PostMapping
    public ResponseEntity<Penalty> createPenalty(@RequestBody Penalty penalty) {
        Penalty saved = penaltyService.createPenalty(penalty);
        logAction("CREATE_PENALTY", saved.getId());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Penalty> updatePenalty(@PathVariable Long id, @RequestBody Penalty penalty) {
        Penalty updated = penaltyService.updatePenalty(id, penalty);
        logAction("UPDATE_PENALTY", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePenalty(@PathVariable Long id) {
        penaltyService.deletePenalty(id);
        logAction("DELETE_PENALTY", id);
        return ResponseEntity.ok().build();
    }

    private void logAction(String action, Long entityId) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityName("Penalty");
        log.setEntityId(entityId);
        // Assuming the principal ID can be extracted if we extended UserDetails. For now leaving userId null or parse username.
        log.setDetails("Action performed by: " + SecurityContextHolder.getContext().getAuthentication().getName());
        auditLogRepository.save(log);
    }
}
