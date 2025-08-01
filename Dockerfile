# # Use OpenJDK 17
# FROM openjdk:17-jdk-alpine AS build

# # Set working directory
# WORKDIR /app

# # Copy Maven wrapper and settings (if you have one)
# COPY mvnw .mvn/ .mvn/


# # Copy the entire project
# COPY . .

# # Make the Maven wrapper executable (if using the wrapper)
# RUN chmod +x mvnw

# # Build the Spring Boot application (skip tests for faster build)
# RUN ./mvnw clean package -DskipTests

# # Second stage - minimal image with just the JAR
# FROM openjdk:17-jdk-alpine

# WORKDIR /app

# # Copy the built JAR file from the previous stage
# COPY --from=build /app/target/ecommerce-backend-0.0.1-SNAPSHOT.jar app.jar

# # Expose the application port
# EXPOSE 8080

# # Run the application
# ENTRYPOINT ["java", "-jar", "app.jar"]


# Use Maven image with JDK 17 for build
FROM maven:3.9.4-eclipse-temurin-17

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src

EXPOSE 8080

CMD ["mvn", "spring-boot:run"]