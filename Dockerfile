FROM eclipse-temurin:17-jdk-jammy

ARG JAR_FILE=target/*.jar
WORKDIR /app

COPY ${JAR_FILE} app.jar
COPY dorms.json dorms.json
COPY entry.sh entry.sh

RUN chmod +x entry.sh

ENTRYPOINT ["./entry.sh"]