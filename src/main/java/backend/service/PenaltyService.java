package backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.entity.Penalty;
import backend.repository.PenaltyRepository;

@Service
public class PenaltyService {

    @Autowired
    private PenaltyRepository repository;

    // CREATE
    public Penalty createPenalty(Penalty penalty) {
        return repository.save(penalty);
    }

    // GET ALL
    public List<Penalty> getAllPenalties() {
        return repository.findAll();
    }

    // GET BY ID
    public Penalty getPenaltyById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Penalty not found"));
    }

    // DELETE
    public void deletePenalty(Long id) {
        repository.deleteById(id);
    }
}