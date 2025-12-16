# Use Eclipse Temurin (OpenJDK) with Debian base
FROM eclipse-temurin:17-jdk

# Set the working directory in the container to /app
WORKDIR /app

# Copy the JAR file into the container at /app
COPY ./target/infra-service-core.jar /app/infra-service.jar

ENV NACOS_SERVER_ADDR=nacos:8848
ENV NACOS_NAMESPACE=prod
ENV ACTIVE_ENV=prod

# Install Docker CLI, gzip and other necessary tools for MySQL backup
RUN apt-get update && \
    apt-get install -y docker.io gzip && \
    rm -rf /var/lib/apt/lists/*

# Make port 8110 available to the world outside this container
EXPOSE 8110

# Run the JAR file
ENTRYPOINT ["java", \
    "-Xms96m", \
    "-Xmx192m", \
    "-XX:MaxMetaspaceSize=128m", \
    "-XX:MaxDirectMemorySize=16m", \
    "-Xss512k", \
    "-XX:ReservedCodeCacheSize=32m", \
    "-XX:+UseSerialGC", \
    "-jar", "infra-service.jar"]
