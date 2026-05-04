package com.tracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AiServiceClient {

    private final RestTemplate restTemplate;

    @Value("${ai-service.url}")
    private String aiServiceUrl;

    public AiServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, String> describePenalty(Map<String, String> request) {
        return restTemplate.postForObject(aiServiceUrl + "/describe", request, Map.class);
    }

    public Map<String, String> recommendActions(Map<String, String> request) {
        return restTemplate.postForObject(aiServiceUrl + "/recommend", request, Map.class);
    }

    public Map<String, String> generateReport(Map<String, Object> request) {
        return restTemplate.postForObject(aiServiceUrl + "/generate-report", request, Map.class);
    }
}
