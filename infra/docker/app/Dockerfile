#FROM gradle:8.4.0-jdk21 AS builder

FROM --platform=$BUILDPLATFORM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
#COPY ../.. .
COPY . .
RUN ./gradlew clean :threadly-apps:app-api:bootJar -x test --no-daemon
RUN ls -al /app/threadly-apps/app-api/build/libs

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/threadly-apps/app-api/build/libs/app*.jar /app/threadly.jar
ENTRYPOINT ["java", "-jar", "/app/threadly.jar"]

