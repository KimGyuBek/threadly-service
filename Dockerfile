FROM openjdk:21-jdk-slim

COPY threadly-apps/app-api/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
