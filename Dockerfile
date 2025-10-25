FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Add application user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the JAR file
COPY build/libs/demo-*.jar app.jar

# Change ownership
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
