package com.mashup.service;

import com.mashup.dto.generated.AgendaResponse;
import com.mashup.dto.generated.EventResponse;
import com.mashup.dto.generated.RecommendationResponse;
import com.mashup.dto.generated.WeatherResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        assertEquals(3.0, agendaService.calculateSpeedup(600, 200));
    }

    @Test
    void calculateSpeedup_WithZeroParallelTime_ShouldReturnSequentialTime() {
        assertEquals(500.0, agendaService.calculateSpeedup(500, 0));
    }

    @Test
    void calculateSpeedup_WithBothTimesZero_ShouldReturnOne() {
        assertEquals(1.0, agendaService.calculateSpeedup(0, 0));
    }

    @Test
    void calculateSpeedup_WithNegativeTimes_ShouldReturnZero() {
        assertEquals(0.0, agendaService.calculateSpeedup(-100, 200));
    }

    @Test
    void buildAgendaParallel_ShouldReturnParallelMode() {
        WeatherResponse weather = new WeatherResponse();
        weather.setCity("Paris");
        weather.setCondition("Clear");
        weather.setFallback(false);

        List<EventResponse> events = List.of(new EventResponse());
        List<RecommendationResponse> recommendations = List.of(new RecommendationResponse());


        when(weatherService.getWeatherCached("Paris")).thenReturn(weather);
        when(eventService.getEventsCached("Paris", "2024-12-25")).thenReturn(events);
        when(recommendationService.getRecommendationsCached("Paris", weather))
                .thenReturn(recommendations);

        AgendaResponse response = agendaService.buildAgendaParallel("Paris", "2024-12-25");

        assertNotNull(response);
        assertEquals("Paris", response.getCity());
        assertEquals("PARALLEL", response.getMode().getValue());
        verify(weatherService).getWeatherCached("Paris");
        verify(eventService).getEventsCached("Paris", "2024-12-25");
        verify(recommendationService).getRecommendationsCached("Paris", weather);
    }

    @Test
    void buildAgendaSequential_ShouldReturnSequentialMode() {
        WeatherResponse weather = new WeatherResponse();
        weather.setCity("Paris");
        weather.setCondition("Clear");
        weather.setFallback(false);

        List<EventResponse> events = List.of(new EventResponse());
        List<RecommendationResponse> recommendations = List.of(new RecommendationResponse());


        when(weatherService.getWeatherCached("Paris")).thenReturn(weather);
        when(eventService.getEventsCached("Paris", "2024-12-25")).thenReturn(events);
        when(recommendationService.getRecommendationsCached("Paris", weather))
                .thenReturn(recommendations);

        AgendaResponse response = agendaService.buildAgendaSequential("Paris", "2024-12-25");

        assertNotNull(response);
        assertEquals("Paris", response.getCity());
        assertEquals("SEQUENTIAL", response.getMode().getValue());
        verify(weatherService).getWeatherCached("Paris");
        verify(eventService).getEventsCached("Paris", "2024-12-25");
        verify(recommendationService).getRecommendationsCached("Paris", weather);
    }
}