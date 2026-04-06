# review-processor

A Spring Boot service that consumes review events from a Kafka topic and persists them to Redis.

## Overview

- Subscribes to a Kafka topic for incoming reviews
- Saves each review to Redis (reactive)
- Built with Spring Boot 4, Java 21

## Running locally

All services communicate over a shared Docker network named `dotohtwolocalinfra`. Both scenarios below use this network — if it already exists (created by the infra project), Docker attaches to it; otherwise it is created fresh.

### With a shared infrastructure project (scenario 1)

Start the infra project first, then run this service with the external override:

```bash
docker compose -f docker-compose.yml -f docker-compose.external.yml up
```

The `review-processor` container joins the existing `dotohtwolocalinfra` network and resolves the infrastructure containers by their hostnames (`kafka`, `redis`, `cassandra`).

### With a local infrastructure (scenario 2)

Spins up Kafka (KRaft mode, no ZooKeeper), Redis, and Cassandra alongside the app:

```bash
docker compose --profile local up
```

## Building

```bash
./mvnw clean package
```
