FROM maven:3-openjdk-17-slim as build

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN ["mvn", "clean", "install"]

FROM openjdk:16
WORKDIR /app
COPY --from=build /app/target/catapp-rest-service-1.0-SNAPSHOT.jar /app/catapp-rest-service.jar
EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "/app/catlog-rest-service.jar"]

LABEL org.opencontainers.image.source https://github.com/sonamsamdupkhangsar/catapp-rest-service