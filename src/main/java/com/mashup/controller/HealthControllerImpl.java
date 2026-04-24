package com.mashup.controller;

import com.mashup.dto.generated.HealthResponse;
import com.mashup.dto.generated.StatusEnum;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Health", description = "️ Monitoring et santé du service")
public class HealthControllerImpl {


    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @GetMapping("/health")
    @Operation(summary = "Vérification de santé",
            description = "Retourne l'état du service (UP/DOWN/DEGRADED)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service en bonne santé",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "success", value = """
                    {
                        "status": "UP",
                        "service": "WeatherEventMashup",
                        "version": "1.0.0",
                        "timestamp": "2026-04-08T10:30:00Z"
                    }
                    """)))
    })
    public ResponseEntity<HealthResponse> healthCheck() {

        boolean anyOpen = circuitBreakerRegistry.getAllCircuitBreakers()
                .stream()
                .anyMatch(cb -> cb.getState() == CircuitBreaker.State.OPEN);

        HealthResponse response = new HealthResponse();
        response.setStatus(anyOpen ? StatusEnum.DEGRADED : StatusEnum.UP);
        response.setService("WeatherEventMashup");
        response.setVersion("1.0.0");
        response.setTimestamp(OffsetDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health/circuit-breakers")
    @Operation(summary = "État des circuit breakers",
            description = "Affiche l'état réel de chaque circuit breaker (CLOSED/OPEN/HALF_OPEN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "États des circuit breakers",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "success", value = """
                    {
                        "weatherApi": "CLOSED",
                        "eventsApi": "CLOSED",
                        "recommendationsApi": "CLOSED"
                    }
                    """)))
    })
    public ResponseEntity<Map<String, String>> getCircuitBreakerStatus() {

        Map<String, String> status = circuitBreakerRegistry.getAllCircuitBreakers()
                .stream()
                .collect(Collectors.toMap(
                        CircuitBreaker::getName,
                        cb -> cb.getState().toString()
                ));
        return ResponseEntity.ok(status);
    }
}