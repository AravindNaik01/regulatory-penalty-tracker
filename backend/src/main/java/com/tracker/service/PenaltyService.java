package com.tracker.service;

import com.tracker.model.Penalty;
import com.tracker.repository.PenaltyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PenaltyService {

    private final PenaltyRepository repository;
    private final EmailService emailService;

    @Cacheable(value = "penalties")
    public List<Penalty> getAllPenalties() {
        return repository.findAll();
    }

    public Penalty getPenaltyById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Penalty not found"));
    }

    @CacheEvict(value = "penalties", allEntries = true)
    public Penalty createPenalty(Penalty penalty) {
        Penalty saved = repository.save(penalty);
        // Optional: send email on high severity
        if ("High".equalsIgnoreCase(penalty.getSeverity())) {
            emailService.sendSimpleMessage("admin@tracker.com", "High Severity Penalty Alert",
                    "A new high severity penalty was added: " + penalty.getTitle());
        }
        return saved;
    }

    @CacheEvict(value = "penalties", allEntries = true)
    public Penalty updatePenalty(Long id, Penalty penaltyDetails) {
        Penalty penalty = getPenaltyById(id);
        penalty.setTitle(penaltyDetails.getTitle());
        penalty.setDescription(penaltyDetails.getDescription());
        penalty.setSeverity(penaltyDetails.getSeverity());
        penalty.setAmount(penaltyDetails.getAmount());
        penalty.setStatus(penaltyDetails.getStatus());
        penalty.setCompanyName(penaltyDetails.getCompanyName());
        return repository.save(penalty);
    }

    @CacheEvict(value = "penalties", allEntries = true)
    public void deletePenalty(Long id) {
        repository.deleteById(id);
    }
}
