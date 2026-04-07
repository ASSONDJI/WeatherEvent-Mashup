package com.mashup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WeatherEventMashupApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherEventMashupApplication.class, args);
		System.out.println("=".repeat(60));
		System.out.println(" WeatherEventMashup Started!");
		System.out.println(" http://localhost:8080");
		System.out.println("=".repeat(60));
	}
}