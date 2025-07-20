# Use Java 17 image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Gradle files
# Copy Gradle wrapper script to the working directory /app/gradlew
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle* .

# Copy src (source code) folder in the working directory /app/src
COPY src src

# Give execution permission to Gradle wrapper
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew build -x test

# Expose port 8080
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "build/libs/sedo-jwt-auth-app-0.0.1-SNAPSHOT.jar"]
