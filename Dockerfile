# syntax = docker/dockerfile:1.2

FROM alpine:latest

COPY /etc/secrets/ /etc/secrets/
RUN cat /etc/secrets/public.pem
