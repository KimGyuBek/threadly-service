FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY threadly-apps/app-api/build/libs/app*.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar"]