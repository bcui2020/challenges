FROM openjdk:11-jre-slim

ENV PORT 8080
EXPOSE $PORT

WORKDIR /app
COPY build/libs/ratelimiter.jar app.jar
COPY run run

RUN adduser --disabled-password --gecos rate-limiter rate-limiter
RUN chmod -R go-w *
USER rate-limiter

CMD ["./run"]
