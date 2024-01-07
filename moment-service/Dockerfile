# Use an official OpenJDK runtime as a parent image
FROM openjdk:17

# Set the working directory in the container to /app
WORKDIR /app

# Copy the JAR file into the container at /app
COPY ./target/moment-service.jar /app/moment-service.jar

ENV NACOS_SERVER_ADDR=nacos:8848
ENV NACOS_NAMESPACE=prod
ENV ACTIVE_ENV=prod

# Install iputils-ping
#RUN apt-get update && \
#    apt-get install -y iputils-ping && \
#    rm -rf /var/lib/apt/lists/*

# Make port 8080 available to the world outside this container
EXPOSE 8130

# Run the JAR file
ENTRYPOINT ["java", "-Xmx256m", "-jar", "moment-service.jar"]
