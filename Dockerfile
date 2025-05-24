# syntax = docker/dockerfile:1.2

FROM gradle:8.13.0-jdk21-alpine AS builder

RUN --mount=type=secret,id=public,dst=/etc/secrets/public.pem cat /etc/secrets/public.pem

WORKDIR /app

COPY build.gradle.kts .
RUN gradle dependencies --no-daemon

COPY . .

RUN mkdir -p src/main/resources/certs/jwts

RUN --mount=type=secret,id=private,dst=/etc/secrets/private.pem \
    --mount=type=secret,id=public,dst=/etc/secrets/public.pem \
    cp /etc/secrets/private.pem \
       /etc/secrets/public.pem \
       /app/src/main/resources/certs/jwts

RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

COPY --from=builder /app/build/libs/ .

CMD java -jar *.jar
