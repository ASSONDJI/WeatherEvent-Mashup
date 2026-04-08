package com.mashup.service;

import com.mashup.dto.generated.RecommendationResponse;
import com.mashup.mapper.RecommendationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationMapper recommendationMapper;

    @Cacheable(value = "recommendations", key = "#city")
    public CompletableFuture<List<RecommendationResponse>> getRecommendations(String city) {
        log.info("Generating recommendations for: {}", city);

        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City parameter cannot be null or empty");
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return recommendationMapper.generateRecommendations(city);
        });
    }

    public CompletableFuture<List<RecommendationResponse>> getRecommendationsFallback(String city, Throwable ex) {
        log.warn(" Recommendation service fallback for: {}", city);
        return CompletableFuture.completedFuture(recommendationMapper.generateFallbackRecommendations(city));
    }
}