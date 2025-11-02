# Étape 1 : Build de l'application Spring Boot avec Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Runtime avec l'image légère Eclipse Temurin 21
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/realestate-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]