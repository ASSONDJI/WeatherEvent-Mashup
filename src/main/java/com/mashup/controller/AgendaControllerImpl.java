package com.mashup.controller;

import com.mashup.dto.generated.AgendaResponse;
import com.mashup.dto.generated.BenchmarkResult;
import com.mashup.dto.generated.RigorousBenchmarkResult;
import com.mashup.service.AgendaService;
import com.mashup.service.BenchmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
            @Parameter(description = "Nom de la ville", example = "Paris", required = true)
            @RequestParam String city,
            @Parameter(description = "Date au format YYYY-MM-DD", example = "2024-12-25", required = true)
            @RequestParam String date) {
        log.info("GET /agenda - city: {}, date: {}", city, date);
        return ResponseEntity.ok(agendaService.buildAgendaParallel(city, date));
    }

    @GetMapping("/agenda/benchmark")
    @Operation(summary = "Comparer les performances séquentiel vs parallèle")
    public ResponseEntity<BenchmarkResult> benchmark(
            @Parameter(description = "Nom de la ville", example = "Paris", required = true)
            @RequestParam String city,
            @Parameter(description = "Date au format YYYY-MM-DD", example = "2024-12-25", required = true)
            @RequestParam String date) {
        log.info("GET /agenda/benchmark - city: {}, date: {}", city, date);
        return ResponseEntity.ok(benchmarkService.runBenchmark(city, date));
    }

    @GetMapping("/agenda/benchmark/rigorous")
    @Operation(summary = "Benchmark rigoureux avec statistiques",
            description = "N itérations avec warmup, moyenne, écart-type et percentile 95")
    public ResponseEntity<RigorousBenchmarkResult> rigorousBenchmark(
            @RequestParam String city,
            @RequestParam String date,
            @RequestParam(defaultValue = "10") int iterations,
            @RequestParam(defaultValue = "3") int warmup) {
        log.info("GET /agenda/benchmark/rigorous - city: {}, iterations: {}, warmup: {}",
                city, iterations, warmup);
        return ResponseEntity.ok(
                benchmarkService.runRigorousBenchmark(city, date, iterations, warmup));
    }
}