# Use Red Hat Universal Base Image with JDK 17
FROM registry.access.redhat.com/ubi8/openjdk-17:1.21-1

# Argument to pass in the JAR_FILE
ARG JAR_FILE=target/*.jar

# Copy your JAR file into the image
COPY ${JAR_FILE} /deployments/app.jar

# Set the default command to execute
# when creating a new container
CMD ["java", "-jar", "/deployments/app.jar"]