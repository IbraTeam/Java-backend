FROM openjdk:17-jdk-oracle 
ENV SPRING_PROFILES_ACTIVE=production
WORKDIR /app
COPY target/JavaBackend-0.0.1-SNAPSHOT.jar /app
CMD ["java", "-jar", "JavaBackend-0.0.1-SNAPSHOT.jar"]
