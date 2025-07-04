FROM gradle:8.4.0-jdk21 AS builder

WORKDIR /app
COPY . .
RUN ./gradlew clean :threadly-apps:app-api:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/threadly-apps/app-api/build/libs/app*.jar /app/threadly.jar
ENTRYPOINT ["java", "-jar", "/app/threadly.jar"]

