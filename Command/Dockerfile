# Use an official OpenJDK 21 runtime as a parent image
FROM openjdk:21-slim

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file from the target directory to the container
COPY target/SpringEventSourcingCommand-0.0.1-SNAPSHOT.jar /app/command-service.jar

# Expose the port the application runs on
EXPOSE 8006

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/command-service.jar"]