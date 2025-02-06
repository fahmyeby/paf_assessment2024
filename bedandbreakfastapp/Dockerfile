# Build stage
FROM maven:3-eclipse-temurin-21 AS builder

WORKDIR /src

COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src

RUN mvn package -e -Dmaven.test.skip=true

# Production stage
FROM maven:3-eclipse-temurin-21
WORKDIR /app

COPY --from=builder /src/target/assessment-0.0.1-SNAPSHOT.jar app.jar

# Set 

ENV PORT=8080
ENV SPRING_DATASOURCE_URL=NOT_SET
ENV SPRING_DATASOURCE_USERNAME=NOT_SET
ENV SPRING_DATASOURCE_PASSWORD=NOT_SET
ENV SPRING_DATA_MONGODB_URI=NOT_SET

EXPOSE ${PORT}

# The corrected entry point to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]