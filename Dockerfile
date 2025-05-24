FROM gradle:8.13.0-jdk21-alpine AS builder

RUN cp /etc/secrets/public.pem ./src/main/resources/certs/jwts/ && \
    cp /etc/secrets/private.pem ./src/main/resources/certs/jwts/

COPY build.gradle.kts .
RUN gradle dependencies --no-daemon

COPY . .
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

COPY --from=builder /home/gradle/build/libs/ .

CMD java -jar *.jar
