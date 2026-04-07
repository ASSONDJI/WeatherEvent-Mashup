package com.mashup.controller;

import com.mashup.dto.generated.AgendaResponse;
import com.mashup.dto.generated.BenchmarkResult;
import com.mashup.service.AgendaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AgendaControllerImpl {

    private final AgendaService agendaService;

    @GetMapping("/agenda")
    public ResponseEntity<AgendaResponse> getAgenda(
            @RequestParam String city,
            @RequestParam String date) {
        log.info(" GET /agenda - city: {}, date: {}", city, date);
        AgendaResponse response = agendaService.buildAgendaParallel(city, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/agenda/benchmark")
    public ResponseEntity<BenchmarkResult> benchmark(
            @RequestParam String city,
            @RequestParam String date) {
        log.info(" GET /agenda/benchmark - city: {}, date: {}", city, date);

        long seqStart = System.currentTimeMillis();
        AgendaResponse sequential = agendaService.buildAgendaSequential(city, date);
        long sequentialTime = System.currentTimeMillis() - seqStart;

        long parStart = System.currentTimeMillis();
        AgendaResponse parallel = agendaService.buildAgendaParallel(city, date);
        long parallelTime = System.currentTimeMillis() - parStart;

        double speedup = (double) sequentialTime / parallelTime;

        BenchmarkResult result = new BenchmarkResult();
        result.setSequentialTimeMs(sequentialTime);
        result.setParallelTimeMs(parallelTime);
        result.setSpeedupFactor(speedup);
        result.setSequentialResponse(sequential);
        result.setParallelResponse(parallel);

        log.info("Benchmark - Sequential: {}ms, Parallel: {}ms, Speedup: {}x",
                sequentialTime, parallelTime, String.format("%.2f", speedup));

        return ResponseEntity.ok(result);
    }
}