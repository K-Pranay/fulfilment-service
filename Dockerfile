# Stage 1: Build application with Maven
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
COPY src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Run container with JRE
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/fulfilment-service-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080
USER 1001

ENTRYPOINT ["java", "-jar", "app.jar"]
