package com.mashup.mapper;

import com.mashup.dto.external.TicketmasterResponse;
import com.mashup.dto.generated.EventResponse;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class EventMapper {

    public EventResponse toEventResponse(TicketmasterResponse.Event event, String city) {
        if (event == null) {
            return null;
        }

        EventResponse response = new EventResponse();
        response.setId(event.getId() != null ? event.getId() : "unknown");
        response.setName(event.getName() != null ? event.getName() : "Unknown Event");
        response.setCity(city != null ? city : "Unknown");
        response.setFallback(false);


        String venue = "Unknown Venue";
        if (event.getEmbedded() != null &&
                event.getEmbedded().getVenues() != null &&
                !event.getEmbedded().getVenues().isEmpty() &&
                event.getEmbedded().getVenues().get(0).getName() != null) {
            venue = event.getEmbedded().getVenues().get(0).getName();
        }
        response.setVenue(venue);


        String category = "General";
        if (event.getClassifications() != null &&
                !event.getClassifications().isEmpty() &&
                event.getClassifications().get(0).getSegment() != null &&
                event.getClassifications().get(0).getSegment().getName() != null) {
            category = event.getClassifications().get(0).getSegment().getName();
        }
        response.setCategory(category);

        return response;
    }

    public List<EventResponse> toEventResponseList(TicketmasterResponse response, String city) {
        if (response == null || response.getEmbedded() == null ||
                response.getEmbedded().getEvents() == null) {
            return Collections.emptyList();
        }

        List<EventResponse> events = new ArrayList<>();
        for (TicketmasterResponse.Event event : response.getEmbedded().getEvents()) {
            EventResponse eventResponse = toEventResponse(event, city);
            if (eventResponse != null) {
                events.add(eventResponse);
            }
        }
        return events;
    }

    public List<EventResponse> toFallbackResponse(String city) {
        EventResponse fallback = new EventResponse();
        fallback.setId("fallback-1");
        fallback.setName("Local Event (Fallback)");
        fallback.setVenue("City Center");
        fallback.setCity(city != null ? city : "Unknown");
        fallback.setCategory("General");
        fallback.setFallback(true);

        List<EventResponse> list = new ArrayList<>();
        list.add(fallback);
        return list;
    }
}