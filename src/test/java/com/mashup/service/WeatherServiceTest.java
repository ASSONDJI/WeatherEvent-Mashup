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
    void shouldThrowExceptionWhenCityIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            weatherService.getWeather(null);
        });
    }

    @Test
    void shouldThrowExceptionWhenCityIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            weatherService.getWeather("");
        });
    }

    @Test
    void shouldReturnMockDataWhenMockEnabled() throws ExecutionException, InterruptedException {
        ReflectionTestUtils.setField(weatherService, "mockEnabled", true);
        String city = "Paris";

        CompletableFuture<WeatherResponse> future = weatherService.getWeather(city);
        WeatherResponse response = future.get();

        assertNotNull(response);
        assertEquals(city, response.getCity());
        assertFalse(response.getFallback());
    }

    @Test
    void shouldReturnMockDataWhenApiKeyMissing() throws ExecutionException, InterruptedException {
        ReflectionTestUtils.setField(weatherService, "apiKey", "");
        String city = "Paris";

        CompletableFuture<WeatherResponse> future = weatherService.getWeather(city);
        WeatherResponse response = future.get();

        assertNotNull(response);
        assertEquals(city, response.getCity());
    }

    @Test
    void shouldReturnFallbackResponse() {
        String city = "Paris";
        Throwable ex = new RuntimeException("API Error");

        WeatherResponse fallback = new WeatherResponse();
        fallback.setCity(city);
        fallback.setFallback(true);
        when(weatherMapper.toFallbackResponse(city)).thenReturn(fallback);

        CompletableFuture<WeatherResponse> future = weatherService.getWeatherFallback(city, ex);
        WeatherResponse response = future.join();

        assertNotNull(response);
        assertTrue(response.getFallback());
        assertEquals(city, response.getCity());
    }
}