package com.mashup.service;

import com.mashup.dto.generated.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {

    @Mock
    private WeatherService weatherService;

    @Mock
    private EventService eventService;

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private AgendaService agendaService;

    @Test
    void calculateSpeedup_WithNormalValues_ShouldReturnCorrectSpeedup() {
        long sequentialTime = 600;
        long parallelTime = 200;
        double speedup = agendaService.calculateSpeedup(sequentialTime, parallelTime);
        assertEquals(3.0, speedup);
    }

    @Test
    void calculateSpeedup_WithZeroParallelTime_ShouldReturnSequentialTime() {
        long sequentialTime = 500;
        long parallelTime = 0;
        double speedup = agendaService.calculateSpeedup(sequentialTime, parallelTime);
        assertEquals(500.0, speedup);
    }

    @Test
    void calculateSpeedup_WithBothTimesZero_ShouldReturnOne() {
        long sequentialTime = 0;
        long parallelTime = 0;
        double speedup = agendaService.calculateSpeedup(sequentialTime, parallelTime);
        assertEquals(1.0, speedup);
    }

    @Test
    void calculateSpeedup_WithNegativeTimes_ShouldReturnZero() {
        long sequentialTime = -100;
        long parallelTime = 200;
        double speedup = agendaService.calculateSpeedup(sequentialTime, parallelTime);
        assertEquals(0.0, speedup);
    }
}