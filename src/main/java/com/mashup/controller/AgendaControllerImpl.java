package com.mashup.controller;

import com.mashup.dto.generated.AgendaResponse;
import com.mashup.dto.generated.BenchmarkResult;
import com.mashup.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Data;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Agenda", description = " Endpoints principaux - Mashup météo + événements + recommandations")
public class AgendaControllerImpl {

    private final AgendaService agendaService;

    @GetMapping("/agenda")
    @Operation(
            summary = "Obtenir l'agenda complet",
            description = """
            Combine en **appels parallèles** :
            - Météo pour la ville
            - Événements culturels
            - Recommandations personnalisées
            
             **Performance** : Temps = max(latences) au lieu de somme
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Agenda construit avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                    {
                        "city": "Paris",
                        "date": "2024-12-25",
                        "weather": {
                            "city": "Paris",
                            "condition": "Sunny",
                            "temperature": 22.5,
                            "feelsLike": 23.0,
                            "humidity": 65,
                            "description": "Pleasant weather",
                            "fallback": false
                        },
                        "events": [
                            {
                                "id": "1",
                                "name": "Jazz Festival",
                                "venue": "Downtown Theater",
                                "city": "Paris",
                                "category": "Music"
                            }
                        ],
                        "recommendations": [
                            {
                                "id": "1",
                                "activity": "Visit Historic Center",
                                "venue": "Paris Old Town",
                                "reason": "Beautiful architecture",
                                "priority": 1,
                                "indoor": false
                            }
                        ],
                        "processingTimeMs": 312,
                        "mode": "PARALLEL"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
            @ApiResponse(responseCode = "404", description = "Ville non trouvée")
    })
    public ResponseEntity<AgendaResponse> getAgenda(
            @Parameter(description = "Nom de la ville", example = "Paris", required = true)
            @RequestParam String city,
            @Parameter(description = "Date au format YYYY-MM-DD", example = "2024-12-25", required = true)
            @RequestParam String date) {
        log.info(" GET /agenda - city: {}, date: {}", city, date);
        AgendaResponse response = agendaService.buildAgendaParallel(city, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/agenda/benchmark")
    @Operation(
            summary = "Comparer les performances",
            description = """
            Compare le temps d'exécution entre :
            - **Mode Séquentiel** : somme des latences
            - **Mode Parallèle** : maximum des latences
            
             **Gain attendu** : 2x à 3x plus rapide
            
             **Note** : Si les temps sont nuls, le facteur d'accélération est ajusté pour éviter les divisions par zéro.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Benchmark complété",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                    {
                        "sequentialTimeMs": 550,
                        "parallelTimeMs": 200,
                        "speedupFactor": 2.75
                    }
                    """
                            )
                    )
            )
    })
    public ResponseEntity<BenchmarkResult> benchmark(
            @Parameter(description = "Nom de la ville", example = "Paris", required = true)
            @RequestParam String city,
            @Parameter(description = "Date au format YYYY-MM-DD", example = "2024-12-25", required = true)
            @RequestParam String date) {
        log.info(" GET /agenda/benchmark - city: {}, date: {}", city, date);

        long seqStart = System.currentTimeMillis();
        AgendaResponse sequential = agendaService.buildAgendaSequential(city, date);
        long sequentialTime = System.currentTimeMillis() - seqStart;

        long parStart = System.currentTimeMillis();
        AgendaResponse parallel = agendaService.buildAgendaParallel(city, date);
        long parallelTime = System.currentTimeMillis() - parStart;


        double speedup = calculateSpeedupSafely(sequentialTime, parallelTime);

        BenchmarkResult result = new BenchmarkResult();
        result.setSequentialTimeMs(sequentialTime);
        result.setParallelTimeMs(parallelTime);
        result.setSpeedupFactor(speedup);
        result.setSequentialResponse(sequential);
        result.setParallelResponse(parallel);

        log.info(" Benchmark - Sequential: {}ms, Parallel: {}ms, Speedup: {}x",
                sequentialTime, parallelTime, String.format("%.2f", speedup));

        return ResponseEntity.ok(result);
    }

    /**
     * Calcule le facteur d'accélération de manière sécurisée.
     *
     * Cas particuliers gérés :
     * - parallelTime == 0 && sequentialTime == 0 → speedup = 1.0 (les deux sont instantanés)
     * - parallelTime == 0 && sequentialTime > 0 → speedup = sequentialTime (parallèle infiniment plus rapide)
     * - parallelTime > 0 && sequentialTime == 0 → speedup = 0 (séquentiel infiniment plus rapide)
     * - parallelTime < 0 ou sequentialTime < 0 → speedup = 0 (temps invalide)
     *
     * @param sequentialTime Temps d'exécution séquentiel en millisecondes
     * @param parallelTime Temps d'exécution parallèle en millisecondes
     * @return Facteur d'accélération (>= 0)
     */
    private double calculateSpeedupSafely(long sequentialTime, long parallelTime) {

        if (sequentialTime < 0 || parallelTime < 0) {
            log.warn("Temps négatifs détectés - seq: {}ms, par: {}ms", sequentialTime, parallelTime);
            return 0.0;
        }


        if (parallelTime == 0) {
            if (sequentialTime == 0) {

                return 1.0;
            }
            return (double) sequentialTime;
        }

        double speedup = (double) sequentialTime / parallelTime;


        if (Double.isInfinite(speedup) || Double.isNaN(speedup)) {
            log.warn("Valeur de speedup invalide détectée - seq: {}ms, par: {}ms", sequentialTime, parallelTime);
            return 0.0;
        }

        return speedup;
    }

    @GetMapping("/agenda/benchmark/rigorous")
    @Operation(
            summary = "Benchmark rigoureux avec itérations",
            description = """
    Effectue N itérations des modes séquentiel et parallèle,
    calcule la moyenne, l'écart-type et le percentile 95.
    
    Paramètres:
    - city: nom de la ville
    - date: date au format YYYY-MM-DD
    - iterations: nombre d'itérations (défaut: 10)
    - warmup: nombre d'itérations d'échauffement (défaut: 3)
    """
    )
    public ResponseEntity<RigorousBenchmarkResult> rigorousBenchmark(
            @RequestParam String city,
            @RequestParam String date,
            @RequestParam(defaultValue = "10") int iterations,
            @RequestParam(defaultValue = "3") int warmup) {

        log.info("📊 Starting rigorous benchmark for {} on {} (iterations: {}, warmup: {})",
                city, date, iterations, warmup);

        // Phase d'échauffement
        for (int i = 0; i < warmup; i++) {
            agendaService.buildAgendaSequential(city, date);
            agendaService.buildAgendaParallel(city, date);
        }

        // Mesures séquentielles
        List<Long> sequentialTimes = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            agendaService.buildAgendaSequential(city, date);
            long end = System.nanoTime();
            sequentialTimes.add((end - start) / 1_000_000);
        }

        // Mesures parallèles
        List<Long> parallelTimes = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            agendaService.buildAgendaParallel(city, date);
            long end = System.nanoTime();
            parallelTimes.add((end - start) / 1_000_000);
        }

        RigorousBenchmarkResult result = new RigorousBenchmarkResult();
        result.setCity(city);
        result.setDate(date);
        result.setIterations(iterations);
        result.setWarmup(warmup);
        result.setSequentialStats(calculateStats(sequentialTimes));
        result.setParallelStats(calculateStats(parallelTimes));
        result.setSpeedupMean(result.getSequentialStats().getMean() / result.getParallelStats().getMean());

        return ResponseEntity.ok(result);
    }

    private Statistics calculateStats(List<Long> times) {
        Statistics stats = new Statistics();
        double mean = times.stream().mapToLong(Long::longValue).average().orElse(0);
        stats.setMean(mean);

        double variance = times.stream()
                .mapToDouble(t -> Math.pow(t - mean, 2))
                .average()
                .orElse(0);
        stats.setStdDev(Math.sqrt(variance));

        List<Long> sorted = new ArrayList<>(times);
        Collections.sort(sorted);
        int p95Index = (int) Math.ceil(0.95 * sorted.size()) - 1;
        stats.setPercentile95(sorted.get(Math.max(0, p95Index)));
        stats.setMin(Collections.min(times));
        stats.setMax(Collections.max(times));
        stats.setCount(times.size());

        return stats;
    }

    @Data
    public static class RigorousBenchmarkResult {
        private String city;
        private String date;
        private int iterations;
        private int warmup;
        private Statistics sequentialStats;
        private Statistics parallelStats;
        private double speedupMean;
    }

    @Data
    public static class Statistics {
        private double mean;
        private double stdDev;
        private long percentile95;
        private long min;
        private long max;
        private int count;
    }
}