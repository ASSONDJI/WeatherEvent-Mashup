package com.mashup.mapper;

import com.mashup.dto.external.OpenWeatherResponse;
import com.mashup.dto.generated.WeatherResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeatherMapperTest {

    private final WeatherMapper mapper = new WeatherMapper();

    @Test
    void shouldMapAllFieldsCorrectly() {
        // Arrange
        OpenWeatherResponse response = new OpenWeatherResponse();
        response.setName("Paris");
        
        OpenWeatherResponse.Main main = new OpenWeatherResponse.Main();
        main.setTemp(22.5);
        main.setFeelsLike(23.0);
        main.setHumidity(65);
        response.setMain(main);
        
        OpenWeatherResponse.Weather weather = new OpenWeatherResponse.Weather();
        weather.setMain("Clear");
        weather.setDescription("clear sky");
        response.setWeather(List.of(weather));
        
        OpenWeatherResponse.Wind wind = new OpenWeatherResponse.Wind();
        wind.setSpeed(5.2);
        response.setWind(wind);

        // Act
        WeatherResponse result = mapper.toWeatherResponse(response);

        // Assert
        assertNotNull(result);
        assertEquals("Paris", result.getCity());
        assertEquals(22.5, result.getTemperature());
        assertEquals(23.0, result.getFeelsLike());
        assertEquals(65, result.getHumidity());
        assertEquals("Clear", result.getCondition());
        assertEquals("clear sky", result.getDescription());
        assertEquals(5.2, result.getWindSpeed());
        assertFalse(result.getFallback());
    }

    @Test
    void shouldReturnNullWhenResponseIsNull() {
        // Act
        WeatherResponse result = mapper.toWeatherResponse(null);

        // Assert
        assertNull(result);
    }

    @Test
    void shouldHandleMissingWindData() {
        // Arrange
        OpenWeatherResponse response = new OpenWeatherResponse();
        response.setName("Paris");
        
        OpenWeatherResponse.Main main = new OpenWeatherResponse.Main();
        main.setTemp(22.5);
        main.setFeelsLike(23.0);
        main.setHumidity(65);
        response.setMain(main);
        
        OpenWeatherResponse.Weather weather = new OpenWeatherResponse.Weather();
        weather.setMain("Clear");
        weather.setDescription("clear sky");
        response.setWeather(List.of(weather));
        
        // Pas de vent

        // Act
        WeatherResponse result = mapper.toWeatherResponse(response);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.getWindSpeed());
    }

    @Test
    void shouldReturnFallbackResponse() {
        // Act
        WeatherResponse result = mapper.toFallbackResponse("Paris");

        // Assert
        assertNotNull(result);
        assertEquals("Paris", result.getCity());
        assertTrue(result.getFallback());
        assertEquals("Unavailable", result.getCondition());
    }

    @Test
    void shouldHandleMissingWeatherData() {
        // Arrange
        OpenWeatherResponse response = new OpenWeatherResponse();
        response.setName("Paris");
        
        OpenWeatherResponse.Main main = new OpenWeatherResponse.Main();
        main.setTemp(22.5);
        main.setFeelsLike(23.0);
        main.setHumidity(65);
        response.setMain(main);
        
        // Pas de météo

        // Act
        WeatherResponse result = mapper.toWeatherResponse(response);

        // Assert
        assertNotNull(result);
        assertEquals("Unknown", result.getCondition());
        assertEquals("No weather data", result.getDescription());
    }
}
