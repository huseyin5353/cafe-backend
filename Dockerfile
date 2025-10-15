# Use official OpenJDK 17 base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Build the application
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Copy the built JAR file
COPY target/restaurantbackend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port (Render uses PORT environment variable)
EXPOSE ${PORT:-8080}

# Run the application
CMD ["java", "-jar", "app.jar"]
