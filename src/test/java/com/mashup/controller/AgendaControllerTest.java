package com.mashup.controller;

import com.mashup.dto.generated.AgendaResponse;
import com.mashup.dto.generated.ModeEnum;
import com.mashup.service.AgendaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendaControllerImpl.class)
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgendaService agendaService;

    @Test
    void getAgenda_WhenCityMissing_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/agenda")
                        .param("date", "2024-12-25"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAgenda_WhenDateMissing_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/agenda")
                        .param("city", "Paris"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAgenda_WhenParametersValid_ShouldReturnOk() throws Exception {

        AgendaResponse mockResponse = new AgendaResponse();
        mockResponse.setCity("Paris");
        mockResponse.setDate("2024-12-25");
        mockResponse.setMode(ModeEnum.PARALLEL);
        when(agendaService.buildAgendaParallel("Paris", "2024-12-25"))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/agenda")
                        .param("city", "Paris")
                        .param("date", "2024-12-25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Paris"))
                .andExpect(jsonPath("$.mode").value("PARALLEL"));
    }

    @Test
    void benchmark_ShouldReturnOk() throws Exception {
        AgendaResponse mockResponse = new AgendaResponse();
        mockResponse.setCity("Paris");
        mockResponse.setMode(ModeEnum.SEQUENTIAL);
        when(agendaService.buildAgendaSequential(anyString(), anyString()))
                .thenReturn(mockResponse);
        when(agendaService.buildAgendaParallel(anyString(), anyString()))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/agenda/benchmark")
                        .param("city", "Paris")
                        .param("date", "2024-12-25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sequentialTimeMs").exists())
                .andExpect(jsonPath("$.speedupFactor").exists());
    }
}