# review-processor

A Spring Boot service that consumes review events from a Kafka topic and persists them to Redis.

## Overview

- Subscribes to a Kafka topic for incoming reviews
- Saves each review to Redis (reactive)
- Built with Spring Boot 4, Java 21

## Running locally

### With a local Kafka and Redis (scenario 1)

Spins up Kafka (KRaft mode, no ZooKeeper) and Redis alongside the app:

```bash
docker compose --profile local up
```

### With an external Kafka and Redis (scenario 2)

If you already have Kafka and Redis running from another project's Docker Compose, just run the app and point it at those instances:

```bash
KAFKA_BOOTSTRAP_SERVERS=kafka:9092 REDIS_HOST=redis docker compose up
```

Or add them to a `.env` file in the project root:

```env
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
REDIS_HOST=redis
REDIS_PORT=6379
```

Then just run:

```bash
docker compose up
```

> If the other project's containers share a Docker network with this one, the default hostnames (`kafka`, `redis`) will resolve automatically and no env vars are needed.

## Building

```bash
./mvnw clean package
```
