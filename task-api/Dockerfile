FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
WORKDIR /app
COPY target/task-api-*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
