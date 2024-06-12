FROM gradle:8.7-jdk21-alpine AS builder

WORKDIR /app
COPY build.gradle.kts gradle.properties settings.gradle.kts ./
RUN gradle shadowJar -x test --no-daemon
COPY src ./src
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache ffmpeg
WORKDIR /app
# todo: user
COPY --from=builder /app/build/libs/*.jar aibot.jar
COPY data data

ENTRYPOINT ["java", "-jar", "aibot.jar"]
