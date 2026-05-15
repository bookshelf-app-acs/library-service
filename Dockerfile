FROM eclipse-temurin:17-jdk-alpine
RUN mkdir -p /usr/src/app
COPY target/*.jar /usr/src/app/app.jar
EXPOSE 8082
CMD ["java", "-jar", "/usr/src/app/app.jar"]