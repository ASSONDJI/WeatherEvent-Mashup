package com.mashup.service;

import com.mashup.dto.generated.WeatherResponse;
import com.mashup.exception.CityNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class WeatherService {

    @Cacheable(value = "weather", key = "#city")
    @CircuitBreaker(name = "weatherApi", fallbackMethod = "getWeatherFallback")
    public CompletableFuture<WeatherResponse> getWeather(String city) {
        log.info(" Fetching weather for: {}", city);

        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City parameter cannot be null or empty");
        }

        if ("invalidcity".equalsIgnoreCase(city)) {
            throw new CityNotFoundException(city);
        }

        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            WeatherResponse response = new WeatherResponse();
            response.setCity(city);
            response.setCondition("Sunny");
            response.setTemperature(22.5);
            response.setFeelsLike(23.0);
            response.setHumidity(65);
            response.setDescription("Pleasant weather, ideal for outdoor activities");
            response.setFallback(false);
            response.setCachedAt(OffsetDateTime.now());

            return response;
        });
    }

    public CompletableFuture<WeatherResponse> getWeatherFallback(String city, Throwable ex) {
        log.warn(" Fallback for weather in: {}", city);

        WeatherResponse fallback = new WeatherResponse();
        fallback.setCity(city);
        fallback.setCondition("Service Unavailable");
        fallback.setDescription("Weather data temporarily unavailable");
        fallback.setFallback(true);
        fallback.setCachedAt(OffsetDateTime.now());

        return CompletableFuture.completedFuture(fallback);
    }
}