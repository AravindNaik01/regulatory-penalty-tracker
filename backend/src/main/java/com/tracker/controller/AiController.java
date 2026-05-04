package com.tracker.controller;

import com.tracker.service.AiServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiServiceClient aiServiceClient;

    @PostMapping("/describe")
    public ResponseEntity<Map<String, String>> describePenalty(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(aiServiceClient.describePenalty(request));
    }

    @PostMapping("/recommend")
    public ResponseEntity<Map<String, String>> recommendActions(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(aiServiceClient.recommendActions(request));
    }

    @PostMapping("/generate-report")
    public ResponseEntity<Map<String, String>> generateReport(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(aiServiceClient.generateReport(request));
    }
}
