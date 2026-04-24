package com.mashup.service;

import com.mashup.dto.generated.AgendaResponse;
import com.mashup.dto.generated.BenchmarkResult;
import com.mashup.dto.generated.BenchmarkStats;
import com.mashup.dto.generated.RigorousBenchmarkResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BenchmarkService {

    private final AgendaService agendaService;

    public BenchmarkResult runBenchmark(String city, String date) {
        log.info("Running benchmark for {} on {}", city, date);

        long seqStart = System.currentTimeMillis();
        AgendaResponse sequential = agendaService.buildAgendaSequential(city, date);
        long sequentialTime = System.currentTimeMillis() - seqStart;

        long parStart = System.currentTimeMillis();
        AgendaResponse parallel = agendaService.buildAgendaParallel(city, date);
        long parallelTime = System.currentTimeMillis() - parStart;

        double speedup = agendaService.calculateSpeedup(sequentialTime, parallelTime);

        log.info("Benchmark - Sequential: {}ms, Parallel: {}ms, Speedup: {}x",
                sequentialTime, parallelTime, String.format("%.2f", speedup));

        BenchmarkResult result = new BenchmarkResult();
        result.setSequentialTimeMs(sequentialTime);
        result.setParallelTimeMs(parallelTime);
        result.setSpeedupFactor(speedup);
        result.setSequentialResponse(sequential);
        result.setParallelResponse(parallel);
        return result;
    }

    public RigorousBenchmarkResult runRigorousBenchmark(
            String city, String date, int iterations, int warmup) {
        log.info("Rigorous benchmark for {} on {} (iterations={}, warmup={})",
                city, date, iterations, warmup);


        for (int i = 0; i < warmup; i++) {
            agendaService.buildAgendaSequential(city, date);
            agendaService.buildAgendaParallel(city, date);
        }


        List<Long> sequentialTimes = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            agendaService.buildAgendaSequential(city, date);
            sequentialTimes.add((System.nanoTime() - start) / 1_000_000);
        }


        List<Long> parallelTimes = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            agendaService.buildAgendaParallel(city, date);
            parallelTimes.add((System.nanoTime() - start) / 1_000_000);
        }

        BenchmarkStats seqStats = calculateStats(sequentialTimes);
        BenchmarkStats parStats = calculateStats(parallelTimes);

        RigorousBenchmarkResult result = new RigorousBenchmarkResult();
        result.setCity(city);
        result.setDate(date);
        result.setIterations(iterations);
        result.setWarmup(warmup);
        result.setSequentialStats(seqStats);
        result.setParallelStats(parStats);
        result.setSpeedupMean(seqStats.getMean() / parStats.getMean());
        return result;
    }

    private BenchmarkStats calculateStats(List<Long> times) {
        double mean = times.stream().mapToLong(Long::longValue).average().orElse(0);

        double variance = times.stream()
                .mapToDouble(t -> Math.pow(t - mean, 2))
                .average()
                .orElse(0);

        List<Long> sorted = new ArrayList<>(times);
        Collections.sort(sorted);
        int p95Index = (int) Math.ceil(0.95 * sorted.size()) - 1;

        BenchmarkStats stats = new BenchmarkStats();
        stats.setMean(mean);
        stats.setStdDev(Math.sqrt(variance));
        stats.setPercentile95(sorted.get(Math.max(0, p95Index)));
        stats.setMin(Collections.min(times));
        stats.setMax(Collections.max(times));
        stats.setCount(times.size());
        return stats;
    }
}