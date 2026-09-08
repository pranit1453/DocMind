# Build Stage
FROM eclipse-temurin:25-jdk AS builder
LABEL authors="Pranit Bhangale"
WORKDIR /app
# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .
RUN chmod +x gradlew
# Copy source
COPY src src
# Build Spring Boot executable jar
RUN ./gradlew clean bootJar --no-daemon

# Runtime Stage
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd -ms /bin/bash spring
COPY --from=builder /app/build/libs/*.jar app.jar
USER spring
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]