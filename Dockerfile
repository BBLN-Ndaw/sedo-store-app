# Use Java 17 image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Gradle files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .

# Copy src (source code) folder in the working directory /app/src
COPY src src

# Give execution permission to Gradle wrapper
RUN chmod +x ./gradlew

# Build the application without running tests
RUN ./gradlew build -x test

# Expose port 8080
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "build/libs/Jwt_auth_app-0.0.1-SNAPSHOT.jar"]
