package com.mashup.service;

import com.mashup.dto.generated.RecommendationResponse;
import com.mashup.dto.generated.WeatherResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.concurrent.ExecutionException;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void getRecommendations_WhenRainyWeather_ShouldReturnIndoorActivities()
            throws ExecutionException, InterruptedException {
        WeatherResponse weather = new WeatherResponse();
        weather.setCondition("Rain");

        List<RecommendationResponse> result =
                recommendationService.getRecommendations("Paris", weather).get();

        assertNotNull(result);
        assertFalse(result.isEmpty());

        assertTrue(result.stream().anyMatch(RecommendationResponse::getIndoor));
    }

    @Test
    void getRecommendations_WhenClearWeather_ShouldReturnOutdoorActivities()
            throws ExecutionException, InterruptedException {
        WeatherResponse weather = new WeatherResponse();
        weather.setCondition("Clear");

        List<RecommendationResponse> result =
                recommendationService.getRecommendations("Paris", weather).get();

        assertNotNull(result);
        assertFalse(result.isEmpty());

        assertTrue(result.stream().anyMatch(r -> !r.getIndoor()));
    }

    @Test
    void getRecommendations_WhenNullWeather_ShouldReturnDefaultRecommendations()
            throws ExecutionException, InterruptedException {
        List<RecommendationResponse> result =
                recommendationService.getRecommendations("Paris", null).get();

        assertNotNull(result);
        assertFalse(result.isEmpty());

        assertEquals("Visit Historic Center", result.get(0).getActivity());
    }

    @Test
    void getRecommendations_ShouldAlwaysIncludeLocalCuisine()
            throws ExecutionException, InterruptedException {
        WeatherResponse weather = new WeatherResponse();
        weather.setCondition("Snow");

        List<RecommendationResponse> result =
                recommendationService.getRecommendations("Paris", weather).get();


        assertTrue(result.stream()
                .anyMatch(r -> r.getActivity().equals("Local Cuisine Experience")));
    }

    @Test
    void getRecommendationsFallback_ShouldReturnFallbackList()
            throws ExecutionException, InterruptedException {
        List<RecommendationResponse> result =
                recommendationService.getRecommendationsFallback("Paris",
                        new RuntimeException("error")).get();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Explore Local Attractions", result.get(0).getActivity());
    }
}