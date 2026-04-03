FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY --from=build /app/target/gateway-0.0.1-SNAPSHOT.jar /app.jar
ENV EUREKA_DEFAULT_ZONE=http://ms-registry-server:8761/eureka/
ENTRYPOINT ["java","-jar","/app.jar"]