package backend.controller;

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
    public Penalty create(@RequestBody Penalty penalty) {
        return service.createPenalty(penalty);
    }

    // GET ALL
    @GetMapping
    public List<Penalty> getAll() {
        return service.getAllPenalties();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public Penalty getById(@PathVariable Long id) {
        return service.getPenaltyById(id);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.deletePenalty(id);
        return "Deleted successfully";
    }
}