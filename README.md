# review-processor

A Spring Boot service that consumes review events from a Kafka topic and persists them to Redis.

## Overview

- Subscribes to a Kafka topic for incoming reviews
- Saves each review to Redis (reactive)
- Built with Spring Boot 4, Java 21

## Running locally

### With a local Kafka broker

Spins up Kafka (KRaft mode, no ZooKeeper) and Redis alongside the app:

```bash
docker compose -f docker-compose.yml -f docker-compose.local.yml up
```

### With an external Kafka broker

Point the app at your own Kafka cluster via the `KAFKA_BOOTSTRAP_SERVERS` environment variable:

```bash
KAFKA_BOOTSTRAP_SERVERS=your-broker:9092 docker compose up
```

Or add it to a `.env` file in the project root:

```env
KAFKA_BOOTSTRAP_SERVERS=your-broker:9092
```

Then just run:

```bash
docker compose up
```

## Building

```bash
./mvnw clean package
```
