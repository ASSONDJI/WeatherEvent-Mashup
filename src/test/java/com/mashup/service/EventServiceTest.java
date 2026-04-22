package com.mashup.service;

import com.mashup.dto.generated.EventResponse;
import com.mashup.mapper.EventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(eventService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(eventService, "mockEnabled", false);
    }

    @Test
    void shouldThrowExceptionWhenCityIsNull() {
        String date = "2024-12-25";
        assertThrows(IllegalArgumentException.class, () -> {
            eventService.getEvents(null, date);
        });
    }

    @Test
    void shouldThrowExceptionWhenCityIsEmpty() {
        String date = "2024-12-25";
        assertThrows(IllegalArgumentException.class, () -> {
            eventService.getEvents("", date);
        });
    }

    @Test
    void shouldThrowExceptionWhenDateIsNull() {
        String city = "Paris";
        assertThrows(IllegalArgumentException.class, () -> {
            eventService.getEvents(city, null);
        });
    }

    @Test
    void shouldThrowExceptionWhenDateIsEmpty() {
        String city = "Paris";
        assertThrows(IllegalArgumentException.class, () -> {
            eventService.getEvents(city, "");
        });
    }

    @Test
    void shouldReturnMockEventsWhenMockEnabled() throws ExecutionException, InterruptedException {
        ReflectionTestUtils.setField(eventService, "mockEnabled", true);
        String city = "Paris";
        String date = "2024-12-25";

        CompletableFuture<List<EventResponse>> future = eventService.getEvents(city, date);
        List<EventResponse> events = future.get();

        assertNotNull(events);
        assertEquals(3, events.size());
        assertEquals("Mock Jazz Festival", events.get(0).getName());
    }

    @Test
    void shouldReturnMockEventsWhenApiKeyMissing() throws ExecutionException, InterruptedException {
        ReflectionTestUtils.setField(eventService, "apiKey", "");
        String city = "Paris";
        String date = "2024-12-25";

        CompletableFuture<List<EventResponse>> future = eventService.getEvents(city, date);
        List<EventResponse> events = future.get();

        assertNotNull(events);
        assertEquals(3, events.size());
    }

    @Test
    void shouldReturnFallbackResponse() {
        String city = "Paris";
        String date = "2024-12-25";
        Throwable ex = new RuntimeException("API Error");

        EventResponse fallbackEvent = new EventResponse();
        fallbackEvent.setId("fallback-1");
        fallbackEvent.setName("Local Event (Fallback)");
        fallbackEvent.setFallback(true);

        when(eventMapper.toFallbackResponse(city)).thenReturn(Arrays.asList(fallbackEvent));

        CompletableFuture<List<EventResponse>> future = eventService.getEventsFallback(city, date, ex);
        List<EventResponse> events = future.join();

        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(events.get(0).getFallback());
    }
}