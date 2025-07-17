FROM eclipse-temurin:17-jdk-jammy

ARG JAR_FILE=target/*.jar
COPY dorms.json dorms.json
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]