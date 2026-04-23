# ============================================
# ÉTAPE 1: BUILD AVEC MAVEN
# ============================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ============================================
# ÉTAPE 2: EXÉCUTION
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copier le JAR
COPY --from=builder /app/target/weather-event-mashup-*.jar app.jar

# ✅ CRUCIAL : Copier explicitement les fichiers de configuration
COPY --from=builder /app/src/main/resources/application.yml ./config/application.yml
COPY --from=builder /app/src/main/resources/application-docker.yml ./config/application-docker.yml

# ✅ Configurer Spring Boot pour utiliser ces fichiers
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker
ENV SPRING_CONFIG_LOCATION=file:./config/
ENV SPRING_CONFIG_NAME=application,application-docker

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/api/v1/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]