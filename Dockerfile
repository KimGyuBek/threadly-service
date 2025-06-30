#FROM openjdk:21-jdk-slim
#
#COPY threadly-apps/app-api/build/libs/*.jar app.jar
#
#EXPOSE 8080
#
#ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM gradle:8.2.1-jdk17 AS builder

WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon

FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY --from=builder /app/threadly-apps/app-api/build/libs/app-api-1.0-SNAPSHOT-boot.jar /app/threadly.jar

ENTRYPOINT ["java", "-jar", "/app/threadly.jar"]
