package backend.controller;

import backend.dto.PenaltyRequest;
import backend.config.ApiResponse;
import backend.entity.Penalty;
import backend.service.PenaltyService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/penalties")
public class PenaltyController {

    @Autowired
    private PenaltyService service;

    // CREATE
    @PostMapping
    public ApiResponse<Penalty> create(@Valid @RequestBody PenaltyRequest request) {

        Penalty penalty = new Penalty();
        penalty.setTitle(request.getTitle());
        penalty.setDescription(request.getDescription());
        penalty.setAmount(request.getAmount());
        penalty.setStatus(request.getStatus());

        Penalty saved = service.createPenalty(penalty);

        return new ApiResponse<>("Created successfully", saved);
    }

    // GET ALL
    @GetMapping
    public ApiResponse<List<Penalty>> getAll() {
        return new ApiResponse<>("Fetched successfully", service.getAllPenalties());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ApiResponse<Penalty> getById(@PathVariable Long id) {
        return new ApiResponse<>("Fetched successfully", service.getPenaltyById(id));
    }

    // DELETE ✅ FIXED
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.deletePenalty(id);
        return new ApiResponse<>("Deleted successfully", null);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ApiResponse<Penalty> update(@PathVariable Long id,
                                      @Valid @RequestBody PenaltyRequest request) {

        Penalty penalty = new Penalty();
        penalty.setTitle(request.getTitle());
        penalty.setDescription(request.getDescription());
        penalty.setAmount(request.getAmount());
        penalty.setStatus(request.getStatus());

        Penalty updated = service.updatePenalty(id, penalty);

        return new ApiResponse<>("Updated successfully", updated);
    }
}