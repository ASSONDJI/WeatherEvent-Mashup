package com.mashup.service;

import com.mashup.dto.external.TicketmasterResponse;
import com.mashup.dto.generated.EventResponse;
import com.mashup.exception.ExternalApiException;
import com.mashup.mapper.EventMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final WebClient webClient;
    private final EventMapper eventMapper;

    @Value("${api.ticketmaster.key:}")
    private String apiKey;

    @Value("${mock.events.enabled:true}")
    private boolean mockEnabled;

    @Cacheable(value = "events", key = "#city + '_' + #date")
    @CircuitBreaker(name = "eventsApi", fallbackMethod = "getEventsFallback")
    public CompletableFuture<List<EventResponse>> getEvents(String city, String date) {
        log.info("🎭 Fetching events for: {} on {}", city, date);

        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City parameter cannot be null or empty");
        }


        if (mockEnabled) {
            log.info(" Using MOCK events service for: {}", city);
            return getMockEvents(city);
        }


        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("No API key for Ticketmaster, using mock");
            return getMockEvents(city);
        }


        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("app.ticketmaster.com")
                        .path("/discovery/v2/events.json")
                        .queryParam("apikey", apiKey)
                        .queryParam("city", city)
                        .queryParam("size", 5)
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    log.error("Ticketmaster API error: {}", response.statusCode().value());
                    throw new ExternalApiException("Ticketmaster", "/events", response.statusCode().value());
                })
                .onStatus(status -> status.is5xxServerError(), response -> {
                    log.error("Ticketmaster server error: {}", response.statusCode().value());
                    throw new ExternalApiException("Ticketmaster", "/events", response.statusCode().value());
                })
                .bodyToMono(TicketmasterResponse.class)
                .map(response -> eventMapper.toEventResponseList(response, city))
                .doOnSuccess(r -> log.info("✅ Found {} events for {}", r.size(), city))
                .doOnError(e -> log.error("❌ Error fetching events for {}: {}", city, e.getMessage()))
                .toFuture();
    }

    private CompletableFuture<List<EventResponse>> getMockEvents(String city) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            EventResponse event1 = new EventResponse();
            event1.setId("mock-1");
            event1.setName("Mock Jazz Festival");
            event1.setVenue("Mock Downtown Theater");
            event1.setCity(city);
            event1.setCategory("Music");
            event1.setFallback(false);

            EventResponse event2 = new EventResponse();
            event2.setId("mock-2");
            event2.setName("Mock Art Exhibition");
            event2.setVenue("Mock City Museum");
            event2.setCity(city);
            event2.setCategory("Art");
            event2.setFallback(false);

            EventResponse event3 = new EventResponse();
            event3.setId("mock-3");
            event3.setName("Mock Food Fair");
            event3.setVenue("Mock Central Square");
            event3.setCity(city);
            event3.setCategory("Gastronomy");
            event3.setFallback(false);

            return List.of(event1, event2, event3);
        });
    }

    public CompletableFuture<List<EventResponse>> getEventsFallback(String city, String date, Throwable ex) {
        log.warn("⚠️ Fallback for events in: {}", city);
        return CompletableFuture.completedFuture(eventMapper.toFallbackResponse(city));
    }
}