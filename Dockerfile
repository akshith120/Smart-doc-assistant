FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src
COPY sample-docs ./sample-docs

RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/smart-doc-assistant.jar /app/smart-doc-assistant.jar
COPY sample-docs ./sample-docs

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/smart-doc-assistant.jar"]