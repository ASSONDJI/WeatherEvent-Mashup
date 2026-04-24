package com.mashup.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Set;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthControllerImpl.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    void healthCheck_WhenAllClosed_ShouldReturnUp() throws Exception {
        CircuitBreaker mockCb = mock(CircuitBreaker.class);
        when(mockCb.getState()).thenReturn(CircuitBreaker.State.CLOSED);
        when(circuitBreakerRegistry.getAllCircuitBreakers())
                .thenReturn(Set.of(mockCb));

        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("WeatherEventMashup"));
    }

    @Test
    void healthCheck_WhenCircuitBreakerOpen_ShouldReturnDegraded() throws Exception {
        CircuitBreaker mockCb = mock(CircuitBreaker.class);
        when(mockCb.getState()).thenReturn(CircuitBreaker.State.OPEN);
        when(circuitBreakerRegistry.getAllCircuitBreakers())
                .thenReturn(Set.of(mockCb));

        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DEGRADED"));
    }

    @Test
    void getCircuitBreakerStatus_ShouldReturnRealStates() throws Exception {
        CircuitBreaker mockCb = mock(CircuitBreaker.class);
        when(mockCb.getName()).thenReturn("weatherApi");
        when(mockCb.getState()).thenReturn(CircuitBreaker.State.CLOSED);
        when(circuitBreakerRegistry.getAllCircuitBreakers())
                .thenReturn(Set.of(mockCb));

        mockMvc.perform(get("/api/v1/health/circuit-breakers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weatherApi").value("CLOSED"));
    }
}