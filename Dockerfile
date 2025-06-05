FROM gradle:8.13.0-jdk21-alpine AS builder

RUN mkdir -p ./src/main/resources/certs/jwts/ && \
    cd ./src/main/resources/certs/jwts/ && \
	openssl genrsa -out keypair.pem 2048 && \
	openssl rsa -in keypair.pem -pubout -out public.pem && \
	openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem

COPY build.gradle.kts .
RUN gradle dependencies --no-daemon

COPY . .
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

COPY --from=builder /home/gradle/build/libs/ .

CMD java -jar *.jar
