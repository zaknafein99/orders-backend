# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon
RUN find /app/build/libs -name "*.jar" -type f

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Set version
ARG VERSION=0.0.1-SNAPSHOT
ENV APP_VERSION=${VERSION}

# Create a non-root user
RUN addgroup --system javauser && adduser --system --no-create-home --ingroup javauser javauser

# Copy the jar from build stage (using wildcard to find the jar)
COPY --from=build /app/build/libs/*.jar app.jar

# Set ownership
RUN chown javauser:javauser /app/app.jar

# Switch to non-root user
USER javauser

EXPOSE 8080

# Set JVM options for container environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Ensure the application binds to all interfaces
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.address=0.0.0.0 -jar /app/app.jar"]
