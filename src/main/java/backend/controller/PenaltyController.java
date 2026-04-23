package backend.controller;
import backend.config.ApiResponse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import backend.entity.Penalty;
import backend.service.PenaltyService;

@RestController
@RequestMapping("/api/penalties")
public class PenaltyController {

    @Autowired
    private PenaltyService service;

 // CREATE
    @PostMapping
    public ApiResponse<Penalty> create(@RequestBody Penalty penalty) {
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

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.deletePenalty(id);
        return "Deleted successfully";
    }
 // UPDATE
    @PutMapping("/{id}")
    public Penalty update(@PathVariable Long id, @RequestBody Penalty penalty) {
        return service.updatePenalty(id, penalty);
    }
}