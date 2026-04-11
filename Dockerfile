# ============================================
# ÉTAPE 1: BUILD AVEC MAVEN ET JAVA 17
# ============================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY src ./src

# Télécharger les dépendances et compiler
RUN mvn clean package -DskipTests

# Lister les fichiers pour déboguer
RUN ls -la target/

# ============================================
# ÉTAPE 2: EXÉCUTION AVEC JAVA 17 JRE
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copier le JAR depuis l'étape de build (adapter le nom)
COPY --from=builder /app/target/weather-event-mashup-*.jar app.jar

# Changer les permissions
RUN chown -R appuser:appgroup /app

# Utiliser l'utilisateur non-root
USER appuser

# Variables d'environnement par défaut
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker

# Port exposé
EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/api/v1/health || exit 1

# Démarrer l'application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]