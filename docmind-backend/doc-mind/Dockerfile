# Build Stage
FROM eclipse-temurin:25-jdk AS builder
LABEL authors="Pranit Bhangale"
WORKDIR /app

# Copy Gradle wrapper and configuration files for layer caching
COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .
RUN chmod +x gradlew

# Copy source code and build executable jar
COPY src src
RUN ./gradlew clean bootJar --no-daemon

# Runtime Stage
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Create non-root system user
RUN useradd -ms /bin/bash spring \
    && mkdir -p /app/logs \
    && chown -R spring:spring /app \

COPY --chown=spring:spring --from=builder /app/build/libs/*.jar app.jar

USER spring
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]