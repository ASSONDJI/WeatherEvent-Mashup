package com.mashup.controller;

import com.mashup.dto.generated.AgendaResponse;
import com.mashup.dto.generated.BenchmarkResult;
import com.mashup.dto.generated.RigorousBenchmarkResult;
import com.mashup.dto.generated.BenchmarkStats;
import com.mashup.dto.generated.ModeEnum;
import com.mashup.service.AgendaService;
import com.mashup.service.BenchmarkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendaControllerImpl.class)
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgendaService agendaService;

    @MockBean
    private BenchmarkService benchmarkService;

    @Test
    void getAgenda_WhenCityMissing_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/agenda")
                        .param("date", "2024-12-25"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAgenda_WhenDateMissing_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/agenda")
                        .param("city", "Paris"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAgenda_WhenParametersValid_ShouldReturnOk() throws Exception {
        AgendaResponse mockResponse = new AgendaResponse();
        mockResponse.setCity("Paris");
        mockResponse.setDate("2024-12-25");
        mockResponse.setMode(ModeEnum.PARALLEL);
        when(agendaService.buildAgendaParallel("Paris", "2024-12-25"))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/agenda")
                        .param("city", "Paris")
                        .param("date", "2024-12-25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Paris"))
                .andExpect(jsonPath("$.mode").value("PARALLEL"));
    }

    @Test
    void benchmark_ShouldReturnBenchmarkResult() throws Exception {
        BenchmarkResult mockResult = new BenchmarkResult();
        mockResult.setSequentialTimeMs(500L);
        mockResult.setParallelTimeMs(200L);
        mockResult.setSpeedupFactor(2.5);
        when(benchmarkService.runBenchmark("Paris", "2024-12-25"))
                .thenReturn(mockResult);

        mockMvc.perform(get("/api/v1/agenda/benchmark")
                        .param("city", "Paris")
                        .param("date", "2024-12-25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sequentialTimeMs").value(500))
                .andExpect(jsonPath("$.parallelTimeMs").value(200))
                .andExpect(jsonPath("$.speedupFactor").value(2.5));
    }

    @Test
    void rigorousBenchmark_ShouldReturnStats() throws Exception {
        BenchmarkStats seqStats = new BenchmarkStats();
        seqStats.setMean(500.0);
        seqStats.setStdDev(10.0);
        seqStats.setPercentile95(520L);
        seqStats.setMin(480L);
        seqStats.setMax(530L);
        seqStats.setCount(10);

        BenchmarkStats parStats = new BenchmarkStats();
        parStats.setMean(200.0);
        parStats.setStdDev(5.0);
        parStats.setPercentile95(210L);
        parStats.setMin(190L);
        parStats.setMax(215L);
        parStats.setCount(10);

        RigorousBenchmarkResult mockResult = new RigorousBenchmarkResult();
        mockResult.setCity("Paris");
        mockResult.setDate("2024-12-25");
        mockResult.setIterations(10);
        mockResult.setWarmup(3);
        mockResult.setSequentialStats(seqStats);
        mockResult.setParallelStats(parStats);
        mockResult.setSpeedupMean(2.5);

        when(benchmarkService.runRigorousBenchmark("Paris", "2024-12-25", 10, 3))
                .thenReturn(mockResult);

        mockMvc.perform(get("/api/v1/agenda/benchmark/rigorous")
                        .param("city", "Paris")
                        .param("date", "2024-12-25")
                        .param("iterations", "10")
                        .param("warmup", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Paris"))
                .andExpect(jsonPath("$.speedupMean").value(2.5))
                .andExpect(jsonPath("$.sequentialStats.mean").value(500.0));
    }
}