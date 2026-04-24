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
        ReflectionTestUtils.setField(eventService, "mockEnabled", true);
    }

    @Test
    void getEvents_WhenMockEnabled_ShouldReturnMockEvents()
            throws ExecutionException, InterruptedException {
        CompletableFuture<List<EventResponse>> future =
                eventService.getEvents("Paris", "2024-12-25");
        List<EventResponse> events = future.get();

        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertEquals("Mock Jazz Festival", events.get(0).getName());
        assertFalse(events.get(0).getFallback());
    }

    @Test
    void getEvents_WhenApiKeyMissing_ShouldReturnMockEvents()
            throws ExecutionException, InterruptedException {
        ReflectionTestUtils.setField(eventService, "apiKey", "");
        ReflectionTestUtils.setField(eventService, "mockEnabled", false);

        CompletableFuture<List<EventResponse>> future =
                eventService.getEvents("Paris", "2024-12-25");
        List<EventResponse> events = future.get();

        assertNotNull(events);
        assertFalse(events.isEmpty());
    }

    @Test
    void getEventsFallback_ShouldReturnFallbackEvents() {
        EventResponse fallbackEvent = new EventResponse();
        fallbackEvent.setId("fallback-1");
        fallbackEvent.setFallback(true);
        when(eventMapper.toFallbackResponse("Paris")).thenReturn(List.of(fallbackEvent));

        CompletableFuture<List<EventResponse>> future =
                eventService.getEventsFallback("Paris", "2024-12-25",
                        new RuntimeException("API Error"));
        List<EventResponse> events = future.join();

        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(events.get(0).getFallback());
        verify(eventMapper).toFallbackResponse("Paris");
    }

    @Test
    void getEventsCached_WhenMockEnabled_ShouldReturnTwoEvents() {
        List<EventResponse> events = eventService.getEventsCached("Lyon", "2024-12-25");

        assertNotNull(events);
        assertEquals(2, events.size());
        assertEquals("Mock Art Exhibition", events.get(1).getName());
    }
}