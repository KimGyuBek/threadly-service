FROM gradle:8.2.1-jdk17 AS builder

WORKDIR /app
COPY . .
RUN ./gradlew :threadly-apps:app-api:bootJar --no-daemon

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/threadly-apps/app-api/build/libs/app-api-1.0-SNAPSHOT-boot.jar /app/threadly.jar
ENTRYPOINT ["java", "-jar", "/app/threadly.jar"]