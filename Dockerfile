# Stage 1: Build dengan Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml dan download dependency (cache step)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build aplikasi dalam production mode
RUN mvn clean install -Pproduction -DskipTests

# Stage 2: Jalankan aplikasi
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy JAR dari stage build
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Jalankan aplikasi
ENTRYPOINT ["java", "-jar", "app.jar"]
