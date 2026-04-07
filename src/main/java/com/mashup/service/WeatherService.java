package com.mashup.service;

import com.mashup.dto.generated.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    @Cacheable(value = "weather", key = "#city")
    public CompletableFuture<WeatherResponse> getWeather(String city) {
        log.info("🌤️ Fetching weather for: {}", city);

        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) {}

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
}