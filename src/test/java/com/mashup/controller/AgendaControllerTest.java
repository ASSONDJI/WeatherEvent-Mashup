package com.mashup.controller;

import com.mashup.service.AgendaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgendaControllerImpl.class)
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgendaService agendaService;

    @Test
    void shouldReturnBadRequestWhenCityMissing() throws Exception {
        mockMvc.perform(get("/api/v1/agenda")
                        .param("date", "2024-12-25"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenDateMissing() throws Exception {
        mockMvc.perform(get("/api/v1/agenda")
                        .param("city", "Paris"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnOkWhenParametersValid() throws Exception {
        mockMvc.perform(get("/api/v1/agenda")
                        .param("city", "Paris")
                        .param("date", "2024-12-25"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBenchmarkResult() throws Exception {
        mockMvc.perform(get("/api/v1/agenda/benchmark")
                        .param("city", "Paris")
                        .param("date", "2024-12-25"))
                .andExpect(status().isOk());
    }
}