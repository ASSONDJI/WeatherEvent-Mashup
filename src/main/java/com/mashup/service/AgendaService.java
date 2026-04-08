package com.mashup.service;

import com.mashup.dto.generated.AgendaResponse;
import com.mashup.dto.generated.ApiStatus;
import com.mashup.dto.generated.EventResponse;
import com.mashup.dto.generated.ModeEnum;
import com.mashup.dto.generated.RecommendationResponse;
import com.mashup.dto.generated.WeatherResponse;
import com.mashup.exception.CityNotFoundException;
import com.mashup.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AgendaService {

    private final WeatherService weatherService;
    private final EventService eventService;
    private final RecommendationService recommendationService;

    @Autowired
    public AgendaService(WeatherService weatherService,
                         EventService eventService,
                         RecommendationService recommendationService) {
        this.weatherService = weatherService;
        this.eventService = eventService;
        this.recommendationService = recommendationService;
    }

    /**
     * Builds agenda in PARALLEL mode.
     * Recommendations are generated AFTER weather is retrieved
     * to adapt suggestions to current weather conditions.
     */
    public AgendaResponse buildAgendaParallel(String city, String date) {
        long startTime = System.currentTimeMillis();
        log.info(" [PARALLEL] Starting agenda for {} on {}", city, date);

        try {

            CompletableFuture<WeatherResponse> weatherFuture = weatherService.getWeather(city);
            CompletableFuture<List<EventResponse>> eventsFuture = eventService.getEvents(city, date);


            WeatherResponse weather = weatherFuture.join();


            CompletableFuture<List<RecommendationResponse>> recommendationsFuture =
                    recommendationService.getRecommendations(city, weather);

            CompletableFuture.allOf(eventsFuture, recommendationsFuture).join();

            long processingTime = System.currentTimeMillis() - startTime;
            log.info("[PARALLEL] Agenda built in {}ms for {}", processingTime, city);

            AgendaResponse response = new AgendaResponse();
            response.setCity(city);
            response.setDate(date);
            response.setWeather(weather);
            response.setEvents(eventsFuture.join());
            response.setRecommendations(recommendationsFuture.join());
            response.setProcessingTimeMs(processingTime);
            response.setMode(ModeEnum.PARALLEL);

            ApiStatus apiStatus = new ApiStatus();
            apiStatus.setWeatherApiAvailable(!response.getWeather().getFallback());
            apiStatus.setEventsApiAvailable(!response.getEvents().isEmpty());
            apiStatus.setRecommendationsApiAvailable(true);
            response.setApiStatus(apiStatus);

            return response;

        } catch (Exception e) {
            log.error("❌ [PARALLEL] Error for {}: {}", city, e.getMessage());

            if (e.getCause() instanceof CityNotFoundException) {
                throw (CityNotFoundException) e.getCause();
            }
            if (e.getCause() instanceof ExternalApiException) {
                throw (ExternalApiException) e.getCause();
            }
            throw new RuntimeException("Failed to build agenda for " + city, e);
        }
    }

    /**
     * Builds agenda in SEQUENTIAL mode (for comparison).
     * Recommendations are generated AFTER weather is retrieved.
     */
    public AgendaResponse buildAgendaSequential(String city, String date) {
        long startTime = System.currentTimeMillis();
        log.info(" [SEQUENTIAL] Starting agenda for {} on {}", city, date);

        try {

            WeatherResponse weather = weatherService.getWeather(city).join();
            List<EventResponse> events = eventService.getEvents(city, date).join();


            List<RecommendationResponse> recommendations =
                    recommendationService.getRecommendations(city, weather).join();

            long processingTime = System.currentTimeMillis() - startTime;
            log.info(" [SEQUENTIAL] Agenda built in {}ms for {}", processingTime, city);

            AgendaResponse response = new AgendaResponse();
            response.setCity(city);
            response.setDate(date);
            response.setWeather(weather);
            response.setEvents(events);
            response.setRecommendations(recommendations);
            response.setProcessingTimeMs(processingTime);
            response.setMode(ModeEnum.SEQUENTIAL);

            ApiStatus apiStatus = new ApiStatus();
            apiStatus.setWeatherApiAvailable(!weather.getFallback());
            apiStatus.setEventsApiAvailable(!events.isEmpty());
            apiStatus.setRecommendationsApiAvailable(true);
            response.setApiStatus(apiStatus);

            return response;

        } catch (Exception e) {
            log.error("❌ [SEQUENTIAL] Error for {}: {}", city, e.getMessage());

            if (e.getCause() instanceof CityNotFoundException) {
                throw (CityNotFoundException) e.getCause();
            }
            if (e.getCause() instanceof ExternalApiException) {
                throw (ExternalApiException) e.getCause();
            }
            throw new RuntimeException("Failed to build sequential agenda for " + city, e);
        }
    }

    public double calculateSpeedup(long sequentialTime, long parallelTime) {
        if (parallelTime == 0) return 0;
        return (double) sequentialTime / parallelTime;
    }
}