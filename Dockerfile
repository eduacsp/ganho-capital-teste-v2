FROM gradle:8.5.0-jdk17 AS build

WORKDIR /app

COPY . .

RUN gradle shadowJar --no-daemon

FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y bash

WORKDIR /app

COPY --from=build /app/build/libs/app.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
