# Use OpenJDK as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy your project JAR to the container
COPY target/ZTools-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render injects a PORT env variable)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
