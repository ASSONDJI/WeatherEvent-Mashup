package com.mashup.dto.external;

import lombok.Data;
import java.util.List;

@Data
public class TicketmasterResponse {
    private Embedded embedded;

    @Data
    public static class Embedded {
        private List<Event> events;
    }

    @Data
    public static class Event {
        private String id;
        private String name;
        private EmbeddedData embedded;
        private List<Classification> classifications;

        @Data
        public static class EmbeddedData {
            private List<Venue> venues;
        }

        @Data
        public static class Venue {
            private String name;
        }

        @Data
        public static class Classification {
            private Segment segment;
        }

        @Data
        public static class Segment {
            private String name;
        }
    }
}