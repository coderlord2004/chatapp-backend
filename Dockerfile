FROM gradle:8.13.0-jdk21-alpine AS builder

WORKDIR /app

COPY build.gradle.kts .
RUN gradle dependencies --no-daemon

COPY . .

RUN --mount=type=bind,source=/etc/secrets/, \
      target=/app/src/main/resources/certs/jwts \
    gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

COPY --from=builder /app/build/libs/ .

CMD java -jar *.jar
