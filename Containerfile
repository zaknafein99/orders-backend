# Build stage
FROM registry.fedoraproject.org/fedora:41 AS build
WORKDIR /app

# Install JDK
RUN dnf -y install java-21-openjdk-devel && \
    dnf clean all

# Copy gradle files first to cache dependencies
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle ./

# Make gradlew executable and download dependencies
RUN chmod +x ./gradlew && \
    ./gradlew dependencies --no-daemon

# Copy source code
COPY . .

# Build the application
RUN ./gradlew build --no-daemon
RUN find /app/build/libs -name "*.jar" -type f

# Run stage
FROM registry.fedoraproject.org/fedora-minimal:41
WORKDIR /app

# Set version
ARG VERSION=0.0.1-SNAPSHOT
ENV APP_VERSION=${VERSION}

# Install JRE and curl for healthcheck
RUN microdnf -y install java-21-openjdk-headless curl && \
    microdnf clean all

# Create a non-root user with a specific UID/GID
RUN groupadd -r javauser -g 1001 && \
    useradd -r -u 1001 -g javauser -s /sbin/nologin -c "Java User" javauser

# Copy the jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Set ownership
RUN chown javauser:javauser /app/app.jar

# Switch to non-root user
USER 1001

EXPOSE 8080

# Set JVM options for container environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dspring.profiles.active=prod"

# Add CORS configuration for frontend
ENV SPRING_WEBFLUX_CORS_ALLOWED_ORIGINS="http://localhost:80,http://frontend:80"
ENV SPRING_MVC_CORS_ALLOWED_ORIGINS="http://localhost:80,http://frontend:80"

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Ensure the application binds to all interfaces and add debug logging
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.address=0.0.0.0 -Dlogging.level.root=INFO -Dlogging.level.org.apache.coyote=WARN -Dlogging.level.org.apache.tomcat=WARN -Dlogging.level.com.zaxxer.hikari=INFO -jar /app/app.jar"] 