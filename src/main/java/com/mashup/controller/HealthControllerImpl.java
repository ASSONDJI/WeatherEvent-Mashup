package com.mashup.controller;

import com.mashup.dto.generated.HealthResponse;
import com.mashup.dto.generated.StatusEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthControllerImpl {

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> healthCheck() {
        HealthResponse response = new HealthResponse();
        response.setStatus(StatusEnum.UP);
        response.setService("WeatherEventMashup");
        response.setVersion("1.0.0");
        response.setTimestamp(OffsetDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health/circuit-breakers")
    public ResponseEntity<Map<String, String>> getCircuitBreakerStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("weatherApi", "CLOSED");
        status.put("eventsApi", "CLOSED");
        status.put("recommendationsApi", "CLOSED");
        return ResponseEntity.ok(status);
    }
}