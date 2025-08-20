# Multi-stage build for batch application
FROM openjdk:21-jdk-slim as builder

WORKDIR /build

# Copy gradle files
COPY gradle gradle
COPY gradlew .
COPY gradle.properties .
COPY settings.gradle .

# Copy all source code
COPY . .

# Build only batch application
RUN ./gradlew :threadly-apps:app-batch:build --no-daemon -x test

# Runtime stage
FROM openjdk:21-jre-slim

WORKDIR /app

# Install additional tools for monitoring
RUN apt-get update && \
    apt-get install -y curl procps && \
    rm -rf /var/lib/apt/lists/*

# Create app user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy built jar
COPY --from=builder /build/threadly-apps/app-batch/build/libs/*.jar app.jar

# Create directories
RUN mkdir -p logs config && \
    chown -R appuser:appuser /app

USER appuser

# JVM optimizations for batch processing
ENV JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs/"

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]