package com.mashup.service;

import com.mashup.dto.generated.RecommendationResponse;
import com.mashup.dto.generated.WeatherResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void getRecommendationsCached_WhenRainyWeather_ShouldReturnIndoorActivities() {
        WeatherResponse weather = new WeatherResponse();
        weather.setCondition("Rain");

        List<RecommendationResponse> result =
                recommendationService.getRecommendationsCached("Paris", weather);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(RecommendationResponse::getIndoor));
    }

    @Test
    void getRecommendationsCached_WhenClearWeather_ShouldReturnOutdoorActivities() {
        WeatherResponse weather = new WeatherResponse();
        weather.setCondition("Clear");

        List<RecommendationResponse> result =
                recommendationService.getRecommendationsCached("Paris", weather);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(r -> !r.getIndoor()));
    }

    @Test
    void getRecommendationsCached_WhenNullWeather_ShouldReturnDefaultRecommendations() {
        List<RecommendationResponse> result =
                recommendationService.getRecommendationsCached("Paris", null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Visit Historic Center", result.get(0).getActivity());
    }

    @Test
    void getRecommendationsCached_ShouldAlwaysIncludeLocalCuisine() {
        WeatherResponse weather = new WeatherResponse();
        weather.setCondition("Snow");

        List<RecommendationResponse> result =
                recommendationService.getRecommendationsCached("Paris", weather);

        assertTrue(result.stream()
                .anyMatch(r -> r.getActivity().equals("Local Cuisine Experience")));
    }

    @Test
    void getRecommendationsFallback_ShouldReturnFallbackList() {
        List<RecommendationResponse> result =
                recommendationService.getRecommendationsFallback("Paris");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Explore Local Attractions", result.get(0).getActivity());
    }
}