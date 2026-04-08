package com.mashup.service;

import com.mashup.dto.generated.RecommendationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class RecommendationService {

    @Cacheable(value = "recommendations", key = "#city")
    public CompletableFuture<List<RecommendationResponse>> getRecommendations(String city) {
        log.info("💡 Generating recommendations for: {}", city);

        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(150); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            RecommendationResponse rec1 = new RecommendationResponse();
            rec1.setId("1");
            rec1.setActivity("Visit Historic Center");
            rec1.setVenue(city + " Old Town");
            rec1.setReason("Beautiful architecture and rich history");
            rec1.setPriority(1);
            rec1.setIndoor(false);

            RecommendationResponse rec2 = new RecommendationResponse();
            rec2.setId("2");
            rec2.setActivity("Local Cuisine Experience");
            rec2.setVenue(city + " Food Market");
            rec2.setReason("Authentic regional dishes");
            rec2.setPriority(2);
            rec2.setIndoor(true);

            RecommendationResponse rec3 = new RecommendationResponse();
            rec3.setId("3");
            rec3.setActivity("Art Museum Tour");
            rec3.setVenue(city + " Art Museum");
            rec3.setReason("Impressive collection of local artists");
            rec3.setPriority(3);
            rec3.setIndoor(true);

            return Arrays.asList(rec1, rec2, rec3);
        });
    }
}