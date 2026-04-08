package com.mashup.service;

import com.mashup.dto.generated.EventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EventService {

    @Cacheable(value = "events", key = "#city + '_' + #date")
    public CompletableFuture<List<EventResponse>> getEvents(String city, String date) {
        log.info(" Fetching events for: {} on {}", city, date);

        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City parameter cannot be null or empty");
        }

        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            EventResponse event1 = new EventResponse();
            event1.setId("1");
            event1.setName("Jazz Festival");
            event1.setVenue("Downtown Theater");
            event1.setCity(city);
            event1.setCategory("Music");
            event1.setFallback(false);

            EventResponse event2 = new EventResponse();
            event2.setId("2");
            event2.setName("Art Exhibition");
            event2.setVenue("City Museum");
            event2.setCity(city);
            event2.setCategory("Art");
            event2.setFallback(false);

            EventResponse event3 = new EventResponse();
            event3.setId("3");
            event3.setName("Food Fair");
            event3.setVenue("Central Square");
            event3.setCity(city);
            event3.setCategory("Gastronomy");
            event3.setFallback(false);

            return Arrays.asList(event1, event2, event3);
        });
    }
}