package com.mashup.service;

import com.mashup.dto.generated.WeatherResponse;
import com.mashup.mapper.WeatherMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private WeatherMapper weatherMapper;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(weatherService, "mockEnabled", false);
    }

    
    @Test
    void getWeatherCached_WhenCityIsNull_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> weatherService.getWeatherCached(null));
    }

    @Test
    void getWeatherCached_WhenCityIsEmpty_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> weatherService.getWeatherCached(""));
    }

    @Test
    void getWeather_WhenMockEnabled_ShouldReturnMockData()
            throws ExecutionException, InterruptedException {
        ReflectionTestUtils.setField(weatherService, "mockEnabled", true);

        CompletableFuture<WeatherResponse> future = weatherService.getWeather("Paris");
        WeatherResponse response = future.get();

        assertNotNull(response);
        assertEquals("Paris", response.getCity());
        assertFalse(response.getFallback());
        assertEquals("Sunny", response.getCondition());
    }

    @Test
    void getWeather_WhenApiKeyMissing_ShouldReturnMockData()
            throws ExecutionException, InterruptedException {
        ReflectionTestUtils.setField(weatherService, "apiKey", "");

        CompletableFuture<WeatherResponse> future = weatherService.getWeather("Paris");
        WeatherResponse response = future.get();

        assertNotNull(response);
        assertEquals("Paris", response.getCity());
        assertFalse(response.getFallback());
    }

    @Test
    void getWeatherFallback_ShouldReturnFallbackResponse() {
        WeatherResponse fallback = new WeatherResponse();
        fallback.setCity("Paris");
        fallback.setFallback(true);
        when(weatherMapper.toFallbackResponse("Paris")).thenReturn(fallback);

        CompletableFuture<WeatherResponse> future =
                weatherService.getWeatherFallback("Paris", new RuntimeException("API Error"));
        WeatherResponse response = future.join();

        assertNotNull(response);
        assertTrue(response.getFallback());
        assertEquals("Paris", response.getCity());
        verify(weatherMapper).toFallbackResponse("Paris");
    }
}