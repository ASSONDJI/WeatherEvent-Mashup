# 🌤️ WeatherEventMashup

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.1-blue)](https://www.openapis.org/)
[![Redis](https://img.shields.io/badge/Redis-7.0-red)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-24.0-blue)](https://www.docker.com/)

##  Description

**WeatherEventMashup** est une application de **mashup** qui combine trois sources de données :
-  **Météo** (OpenWeatherMap API)
-  **Événements culturels** (Ticketmaster API)
-  **Recommandations personnalisées** (service interne)

L'application démontre les concepts avancés de **middleware** pour les systèmes distribués :
- **Appels parallèles** avec `CompletableFuture.allOf()`
- **Cache distribué** avec Redis
- **Circuit Breaker** avec Resilience4j
- **Génération automatique** de code via OpenAPI

##  Objectifs Pédagogiques

Ce projet illustre les concepts clés du cours **Introduction à l'Intergiciel** :

| Concept | Implémentation |
|---------|----------------|
| Composition de services SOA (Mashup) | Combinaison de 3 APIs distinctes |
| Appels parallèles | `CompletableFuture.allOf()` - temps total = max(latences) |
| WebClient (réactif) | Appels HTTP non-bloquants |
| Cache (Fielding) | Redis avec TTL 10 minutes |
| Circuit Breaker | Resilience4j pour la résilience |

##  Performance

L'approche parallèle offre un gain significatif :
Mode Séquentiel : ~900ms (Somme des 3 appels)
Mode Parallèle : ~300ms (Maximum des 3 appels)
Gain : 3x plus rapide !


##  Stack Technique

| Composant | Technologie | Version |
|-----------|-------------|---------|
| Framework | Spring Boot | 3.2.4 |
| Langage | Java | 17 |
| Cache | Redis | 7.0 |
| Containerisation | Docker | 24.0 |
| Documentation API | OpenAPI | 3.1 |
| Génération code | OpenAPI Generator | 7.5.0 |
| Circuit Breaker | Resilience4j | 2.2.0 |
| Mapping | MapStruct | 1.5.5 |
| Réduction boilerplate | Lombok | latest |

##  Architecture
```bash
┌─────────────────┐
│   Client HTTP   │
└────────┬────────┘
│
▼
┌─────────────────┐
│   Controller    │
└────────┬────────┘
│
▼
┌─────────────────┐
│  AgendaService  │
└────────┬────────┘
│
┌────────────────────┼────────────────────┐
│                    │                    │
▼                    ▼                    ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│WeatherService │   │ EventService  │   │   RecoService │
│   (Météo)     │   │ (Événements)  │   │ (Recommand.)  │
└───────┬───────┘   └───────┬───────┘   └───────┬───────┘
│                   │                   │
└───────────────────┼───────────────────┘
│
┌───────▼───────┐
│  Redis Cache  │
└───────────────┘
```


##  Quick Start

### Prérequis

```bash
# Java 17+
java -version

# Docker & Docker Compose
docker --version
docker-compose --version

# Maven
mvn --version
```
## Installation
# 1. Cloner le projet
git clone https://github.com/ASSONDJI/WeatherEventMashup.git
cd WeatherEventMashup

# 2. Démarrer Redis avec Docker
docker-compose up -d

# 3. Compiler le projet
mvn clean compile

# 4. Lancer l'application
mvn spring-boot:run

## Configuration des clés API (Optionnel)
Pour utiliser les vraies APIs, définissez les variables d'environnement :
export OPENWEATHER_API_KEY=votre_clé_ici
export TICKETMASTER_API_KEY=votre_clé_ici

## Documentation API
Une fois l'application démarrée :

Swagger UI : http://localhost:8080/swagger-ui.html

OpenAPI JSON : http://localhost:8080/api-docs

## Endpoints disponibles
*Méthode	Endpoint	Description*
* GET	/api/v1/health	Vérification de santé du service
* GET	/api/v1/agenda?city=Paris&date=2024-12-25	Agenda complet (appels parallèles)
* GET	/api/v1/agenda/benchmark?city=Paris&date=2024-12-25	Benchmark séquentiel vs parallèle
* GET	/api/v1/health/circuit-breakers	Statut des circuit breakers

## Tests
# Health check
curl http://localhost:8080/api/v1/health

# Agenda (mode parallèle)
curl "http://localhost:8080/api/v1/agenda?city=Paris&date=2024-12-25"

# Benchmark
curl "http://localhost:8080/api/v1/agenda/benchmark?city=Paris&date=2024-12-25"
## Exemple de Réponse
```bash
{
"city": "Paris",
"date": "2024-12-25",
"weather": {
"city": "Paris",
"condition": "Sunny",
"temperature": 22.5,
"feelsLike": 23.0,
"humidity": 65,
"description": "Pleasant weather"
},
"events": [
{
"id": "1",
"name": "Jazz Festival",
"venue": "Downtown Theater",
"category": "Music"
}
],
"recommendations": [
{
"activity": "Visit Historic Center",
"venue": "Paris Old Town",
"priority": 1
}
],
"processingTimeMs": 312,
"mode": "PARALLEL"
}
```
## STRUCTURE SIMPLE ET PROFESSIONNELLE DU PROJET
```bash
WeatherEventMashup/
│
├── src/
│   └── main/
│       ├── java/com/mashup/
│       │   ├── WeatherEventMashupApplication.java
│       │   │
│       │   ├── config/
│       │   │   ├── WebClientConfig.java
│       │   │   └── RedisConfig.java
│       │   │
│       │   ├── controller/
│       │   │   ├── AgendaController.java
│       │   │   └── HealthController.java
│       │   │
│       │   ├── service/
│       │   │   ├── AgendaService.java
│       │   │   ├── WeatherService.java
│       │   │   ├── EventService.java
│       │   │   └── RecommendationService.java
│       │   │
│       │   ├── dto/
│       │   │   └── generated/          # Généré par OpenAPI
│       │   │       ├── WeatherResponse.java
│       │   │       ├── EventResponse.java
│       │   │       ├── RecommendationResponse.java
│       │   │       └── AgendaResponse.java
│       │   │
│       │   ├── api/                    # Généré par OpenAPI
│       │   │   ├── AgendaApi.java
│       │   │   └── HealthApi.java
│       │   │
│       │   └── exception/
│       │       ├── CityNotFoundException.java
│       │       └── GlobalExceptionHandler.java
│       │
│       └── resources/
│           ├── application.yml
│           └── openapi.yaml
│
├── docker-compose.yml
├── pom.xml
└── README.md
```
## FLUX D'EXÉCUTION

1. Client → GET /api/v1/agenda?city=Paris
2. Controller → reçoit la requête
3. AgendaService → lance 3 appels en PARALLÈLE
4. WeatherService → interroge API météo (ou mock)
5. EventService → interroge API événements (ou mock)
6. RecoService → génère recommandations
7. Agrégation → combine les 3 résultats
8. Réponse → JSON renvoyé au client

## TEMPS D'EXÉCUTION

* Mode SÉQUENTIEL :  Weather + Events + Recos = 550ms
* Mode PARALLÈLE  :  MAX(Weather, Events, Recos) = 200ms
* GAIN : 2.75x plus rapide 
## Auteur
**Étudiant : MALAIKA LADEESSE ASSONDJI**

**Encadré par : Pr. BOMGNI Alain Bertrand**

* Cours : Introduction à l'Intergiciel (Middleware)
* Année académique : 2025/2026

 **Licence
MIT**

## Remerciements
* OpenWeatherMap pour l'API météo
* Ticketmaster pour l'API événements
* La communauté OpenAPI Generator