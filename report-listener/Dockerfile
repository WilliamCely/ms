FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/report-listener-0.0.1-SNAPSHOT.jar app.jar
ENV EUREKA_DEFAULT_ZONE=http://localhost:8761/eureka/
ENV MONGO_HOST=null
ENV KAFKA_BROKER=localhost:9092
ENTRYPOINT ["java","-jar","/app.jar"]