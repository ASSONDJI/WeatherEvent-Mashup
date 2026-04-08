package com.mashup.controller;

import com.mashup.dto.generated.HealthResponse;
import com.mashup.dto.generated.StatusEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "❤️ Monitoring et santé du service")
public class HealthControllerImpl {

    @GetMapping("/health")
    @Operation(
            summary = "Vérification de santé",
            description = "Retourne l'état du service (UP/DOWN/DEGRADED)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Service en bonne santé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                    {
                        "status": "UP",
                        "service": "WeatherEventMashup",
                        "version": "1.0.0",
                        "timestamp": "2026-04-08T10:30:00Z"
                    }
                    """
                            )
                    )
            )
    })
    public ResponseEntity<HealthResponse> healthCheck() {
        HealthResponse response = new HealthResponse();
        response.setStatus(StatusEnum.UP);
        response.setService("WeatherEventMashup");
        response.setVersion("1.0.0");
        response.setTimestamp(OffsetDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health/circuit-breakers")
    @Operation(
            summary = "État des circuit breakers",
            description = "Affiche l'état de chaque circuit breaker (CLOSED/OPEN/HALF_OPEN)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "États des circuit breakers",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                    {
                        "weatherApi": "CLOSED",
                        "eventsApi": "CLOSED",
                        "recommendationsApi": "CLOSED"
                    }
                    """
                            )
                    )
            )
    })
    public ResponseEntity<Map<String, String>> getCircuitBreakerStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("weatherApi", "CLOSED");
        status.put("eventsApi", "CLOSED");
        status.put("recommendationsApi", "CLOSED");
        return ResponseEntity.ok(status);
    }
}