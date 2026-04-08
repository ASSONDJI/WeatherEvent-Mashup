package com.mashup.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI weatherEventMashupOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(servers())
                .tags(tags());
    }

    private Info apiInfo() {
        return new Info()
                .title("WeatherEvent Mashup API")
                .version("1.0.0")
                .description("API that combines weather, cultural events and personalized recommendations.")
                .contact(contact())
                .license(license());
    }

    private Contact contact() {
        return new Contact()
                .name("Malaïka Ladéesse Assondji")
                .email("assondji.malaika@example.com");
    }

    private License license() {
        return new License()
                .name("MIT")
                .url("https://opensource.org/licenses/MIT");
    }

    private List<Server> servers() {
        return List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Local server")
        );
    }

    private List<Tag> tags() {
        return List.of(
                new Tag().name("agenda").description("Agenda operations"),
                new Tag().name("health").description("Health check operations")
        );
    }
}