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
    void shouldGenerateRecommendationsBasedOnWeather() throws ExecutionException, InterruptedException {
        // Arrange
        String city = "Paris";
        WeatherResponse weather = new WeatherResponse();
        weather.setCondition("Rain");

        // Act
        List<RecommendationResponse> result = recommendationService.getRecommendations(city, weather).get();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldGenerateFallbackRecommendations() throws ExecutionException, InterruptedException {
        // Arrange
        String city = "Paris";

        // Act
        List<RecommendationResponse> result = recommendationService.getRecommendations(city).get();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Explore Local Attractions", result.get(0).getActivity());
    }
}