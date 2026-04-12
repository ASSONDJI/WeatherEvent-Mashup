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
            log.info(" [PARALLEL] Agenda built in {}ms for {}", processingTime, city);

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
            log.error("[PARALLEL] Error for {}: {}", city, e.getMessage());

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
            log.info("[SEQUENTIAL] Agenda built in {}ms for {}", processingTime, city);

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
            log.error(" [SEQUENTIAL] Error for {}: {}", city, e.getMessage());

            if (e.getCause() instanceof CityNotFoundException) {
                throw (CityNotFoundException) e.getCause();
            }
            if (e.getCause() instanceof ExternalApiException) {
                throw (ExternalApiException) e.getCause();
            }
            throw new RuntimeException("Failed to build sequential agenda for " + city, e);
        }
    }

    /**
     * Calcule le facteur d'accélération de manière sécurisée.
     *
     * Cas particuliers gérés :
     * - parallelTime == 0 && sequentialTime == 0 → speedup = 1.0 (les deux sont instantanés)
     * - parallelTime == 0 && sequentialTime > 0 → speedup = sequentialTime (parallèle infiniment plus rapide)
     * - parallelTime > 0 && sequentialTime == 0 → speedup = 0 (séquentiel infiniment plus rapide)
     * - parallelTime < 0 ou sequentialTime < 0 → speedup = 0 (temps invalide)
     *
     * @param sequentialTime Temps d'exécution séquentiel en millisecondes
     * @param parallelTime Temps d'exécution parallèle en millisecondes
     * @return Facteur d'accélération (>= 0)
     */
    public double calculateSpeedup(long sequentialTime, long parallelTime) {

        if (sequentialTime < 0 || parallelTime < 0) {
            log.warn("Temps négatifs détectés - seq: {}ms, par: {}ms", sequentialTime, parallelTime);
            return 0.0;
        }


        if (parallelTime == 0) {
            if (sequentialTime == 0) {

                return 1.0;
            }

            return (double) sequentialTime;
        }


        double speedup = (double) sequentialTime / parallelTime;


        if (Double.isInfinite(speedup) || Double.isNaN(speedup)) {
            log.warn("Valeur de speedup invalide détectée - seq: {}ms, par: {}ms", sequentialTime, parallelTime);
            return 0.0;
        }

        return speedup;
    }
}