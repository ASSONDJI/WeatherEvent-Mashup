package com.mashup.service;

import com.mashup.dto.generated.AgendaResponse;
import com.mashup.dto.generated.ApiStatus;
import com.mashup.dto.generated.EventResponse;
import com.mashup.dto.generated.ModeEnum;
import com.mashup.dto.generated.RecommendationResponse;
import com.mashup.dto.generated.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgendaService {

    private final WeatherService weatherService;
    private final EventService eventService;
    private final RecommendationService recommendationService;

    public AgendaResponse buildAgendaParallel(String city, String date) {
        long startTime = System.currentTimeMillis();
        log.info(" Building agenda for {} (PARALLEL)", city);

        CompletableFuture<WeatherResponse> weatherFuture = weatherService.getWeather(city);
        CompletableFuture<List<EventResponse>> eventsFuture = eventService.getEvents(city, date);
        CompletableFuture<List<RecommendationResponse>> recommendationsFuture =
                recommendationService.getRecommendations(city);

        CompletableFuture.allOf(weatherFuture, eventsFuture, recommendationsFuture).join();

        long processingTime = System.currentTimeMillis() - startTime;
        log.info(" Agenda built in {}ms (PARALLEL)", processingTime);

        AgendaResponse response = new AgendaResponse();
        response.setCity(city);
        response.setDate(date);
        response.setWeather(weatherFuture.join());
        response.setEvents(eventsFuture.join());
        response.setRecommendations(recommendationsFuture.join());
        response.setProcessingTimeMs(processingTime);
        response.setMode(ModeEnum.PARALLEL);

        ApiStatus apiStatus = new ApiStatus();
        apiStatus.setWeatherApiAvailable(true);
        apiStatus.setEventsApiAvailable(true);
        apiStatus.setRecommendationsApiAvailable(true);
        response.setApiStatus(apiStatus);

        return response;
    }

    public AgendaResponse buildAgendaSequential(String city, String date) {
        long startTime = System.currentTimeMillis();
        log.info(" Building agenda for {} (SEQUENTIAL)", city);

        WeatherResponse weather = weatherService.getWeather(city).join();
        List<EventResponse> events = eventService.getEvents(city, date).join();
        List<RecommendationResponse> recommendations = recommendationService.getRecommendations(city).join();

        long processingTime = System.currentTimeMillis() - startTime;
        log.info(" Agenda built in {}ms (SEQUENTIAL)", processingTime);

        AgendaResponse response = new AgendaResponse();
        response.setCity(city);
        response.setDate(date);
        response.setWeather(weather);
        response.setEvents(events);
        response.setRecommendations(recommendations);
        response.setProcessingTimeMs(processingTime);
        response.setMode(ModeEnum.SEQUENTIAL);

        ApiStatus apiStatus = new ApiStatus();
        apiStatus.setWeatherApiAvailable(true);
        apiStatus.setEventsApiAvailable(true);
        apiStatus.setRecommendationsApiAvailable(true);
        response.setApiStatus(apiStatus);

        return response;
    }
}