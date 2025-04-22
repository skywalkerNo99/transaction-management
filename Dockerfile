# Build stage
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Create a non-root user
RUN useradd -r -u 1001 -g root appuser
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 