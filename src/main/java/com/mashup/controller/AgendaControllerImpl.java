package com.mashup.controller;

import com.mashup.dto.generated.AgendaResponse;
import com.mashup.dto.generated.BenchmarkResult;
import com.mashup.dto.generated.RigorousBenchmarkResult;
import com.mashup.service.AgendaService;
import com.mashup.service.BenchmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Agenda", description = "Endpoints principaux - Mashup météo + événements + recommandations")
public class AgendaControllerImpl {

    private final AgendaService agendaService;
    private final BenchmarkService benchmarkService;

    @GetMapping("/agenda")
    @Operation(summary = "Obtenir l'agenda complet",
            description = "Combine météo, événements et recommandations en appels parallèles")
    public ResponseEntity<AgendaResponse> getAgenda(
            @Parameter(description = "Nom de la ville (2-100 caractères)", example = "Paris", required = true)
            @RequestParam
            @NotBlank(message = "City cannot be blank")
            @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "City must contain only letters, spaces or hyphens")
            String city,

            @Parameter(description = "Date au format YYYY-MM-DD", example = "2024-12-25", required = true)
            @RequestParam
            @NotBlank(message = "Date cannot be blank")
            @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in format YYYY-MM-DD")
            String date) {
        log.info("GET /agenda - city: {}, date: {}", city, date);
        return ResponseEntity.ok(agendaService.buildAgendaParallel(city, date));
    }

    @GetMapping("/agenda/benchmark")
    @Operation(summary = "Comparer les performances séquentiel vs parallèle")
    public ResponseEntity<BenchmarkResult> benchmark(
            @Parameter(description = "Nom de la ville", example = "Paris", required = true)
            @RequestParam
            @NotBlank(message = "City cannot be blank")
            String city,

            @Parameter(description = "Date au format YYYY-MM-DD", example = "2024-12-25", required = true)
            @RequestParam
            @NotBlank(message = "Date cannot be blank")
            @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in format YYYY-MM-DD")
            String date) {
        log.info("GET /agenda/benchmark - city: {}, date: {}", city, date);
        return ResponseEntity.ok(benchmarkService.runBenchmark(city, date));
    }

    @GetMapping("/agenda/benchmark/rigorous")
    @Operation(summary = "Benchmark rigoureux avec statistiques",
            description = "N itérations avec warmup, moyenne, écart-type et percentile 95")
    public ResponseEntity<RigorousBenchmarkResult> rigorousBenchmark(
            @RequestParam
            @NotBlank(message = "City cannot be blank")
            String city,

            @RequestParam
            @NotBlank(message = "Date cannot be blank")
            @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in format YYYY-MM-DD")
            String date,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Iterations must be at least 1")
            @Max(value = 50, message = "Iterations must be at most 50")
            int iterations,

            @RequestParam(defaultValue = "3")
            @Min(value = 0, message = "Warmup must be at least 0")
            @Max(value = 10, message = "Warmup must be at most 10")
            int warmup) {
        log.info("GET /agenda/benchmark/rigorous - city: {}, iterations: {}, warmup: {}",
                city, iterations, warmup);
        return ResponseEntity.ok(
                benchmarkService.runRigorousBenchmark(city, date, iterations, warmup));
    }
}