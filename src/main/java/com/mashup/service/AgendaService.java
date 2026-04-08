package com.mashup.service;

import com.mashup.dto.generated.AgendaResponse;
import com.mashup.dto.generated.ApiStatus;
import com.mashup.dto.generated.EventResponse;
import com.mashup.dto.generated.ModeEnum;
import com.mashup.dto.generated.RecommendationResponse;
import com.mashup.dto.generated.WeatherResponse;
import com.mashup.exception.CityNotFoundException;
import com.mashup.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service orchestrant les appels parallèles aux 3 sources de données.
 *
 * Principes démontrés :
 * - Composition de services SOA (mashup)
 * - Appels parallèles avec CompletableFuture.allOf()
 * - Temps total = MAX(latence_météo, latence_events, latence_recos)
 * - Propagation des exceptions métier
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgendaService {

    private final WeatherService weatherService;
    private final EventService eventService;
    private final RecommendationService recommendationService;

    /**
     * Construit l'agenda en MODE PARALLÈLE.
     * Les 3 appels sont lancés simultanément.
     * Temps d'exécution = MAX des 3 latences.
     *
     * @param city Ville recherchée
     * @param date Date des événements
     * @return AgendaResponse contenant météo + événements + recommandations
     * @throws CityNotFoundException Si la ville n'existe pas
     * @throws ExternalApiException Si une API externe est indisponible
     */
    public AgendaResponse buildAgendaParallel(String city, String date) {
        long startTime = System.currentTimeMillis();
        log.info(" [PARALLEL] Début de l'agenda pour {} le {}", city, date);

        try {

            CompletableFuture<WeatherResponse> weatherFuture = weatherService.getWeather(city);
            CompletableFuture<List<EventResponse>> eventsFuture = eventService.getEvents(city, date);
            CompletableFuture<List<RecommendationResponse>> recommendationsFuture =
                    recommendationService.getRecommendations(city);


            CompletableFuture.allOf(weatherFuture, eventsFuture, recommendationsFuture).join();

            long processingTime = System.currentTimeMillis() - startTime;
            log.info(" [PARALLEL] Agenda construit en {}ms pour {}", processingTime, city);


            AgendaResponse response = new AgendaResponse();
            response.setCity(city);
            response.setDate(date);
            response.setWeather(weatherFuture.join());
            response.setEvents(eventsFuture.join());
            response.setRecommendations(recommendationsFuture.join());
            response.setProcessingTimeMs(processingTime);
            response.setMode(ModeEnum.PARALLEL);

            // État des APIs (pour le monitoring)
            ApiStatus apiStatus = new ApiStatus();
            apiStatus.setWeatherApiAvailable(!response.getWeather().getFallback());
            apiStatus.setEventsApiAvailable(!response.getEvents().isEmpty());
            apiStatus.setRecommendationsApiAvailable(true);
            response.setApiStatus(apiStatus);

            return response;

        } catch (Exception e) {
            // Propagation des exceptions métier
            log.error("❌ [PARALLEL] Erreur pour {}: {}", city, e.getMessage());

            if (e.getCause() instanceof CityNotFoundException) {
                throw (CityNotFoundException) e.getCause();
            }
            if (e.getCause() instanceof ExternalApiException) {
                throw (ExternalApiException) e.getCause();
            }
            throw new RuntimeException("Erreur lors de la construction de l'agenda pour " + city, e);
        }
    }

    /**
     * Construit l'agenda en MODE SÉQUENTIEL (pour comparaison).
     * Les 3 appels sont exécutés l'un après l'autre.
     * Temps d'exécution = SOMME des 3 latences.
     *
     * @param city Ville recherchée
     * @param date Date des événements
     * @return AgendaResponse contenant météo + événements + recommandations
     */
    public AgendaResponse buildAgendaSequential(String city, String date) {
        long startTime = System.currentTimeMillis();
        log.info(" [SEQUENTIAL] Début de l'agenda pour {} le {}", city, date);

        try {

            WeatherResponse weather = weatherService.getWeather(city).join();
            List<EventResponse> events = eventService.getEvents(city, date).join();
            List<RecommendationResponse> recommendations = recommendationService.getRecommendations(city).join();

            long processingTime = System.currentTimeMillis() - startTime;
            log.info("✅ [SEQUENTIAL] Agenda construit en {}ms pour {}", processingTime, city);

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
            log.error("❌ [SEQUENTIAL] Erreur pour {}: {}", city, e.getMessage());

            if (e.getCause() instanceof CityNotFoundException) {
                throw (CityNotFoundException) e.getCause();
            }
            if (e.getCause() instanceof ExternalApiException) {
                throw (ExternalApiException) e.getCause();
            }
            throw new RuntimeException("Erreur lors de la construction séquentielle pour " + city, e);
        }
    }

    /**
     * Calcule le gain de performance du mode parallèle par rapport au mode séquentiel.
     *
     * @param sequentialTime Temps séquentiel en ms
     * @param parallelTime Temps parallèle en ms
     * @return Facteur d'accélération (ex: 3.5x)
     */
    public double calculateSpeedup(long sequentialTime, long parallelTime) {
        if (parallelTime == 0) return 0;
        return (double) sequentialTime / parallelTime;
    }
}