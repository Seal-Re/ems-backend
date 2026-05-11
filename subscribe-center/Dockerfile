FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/subscribe-center-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 11451
ENTRYPOINT ["java", "-jar", "app.jar"]
