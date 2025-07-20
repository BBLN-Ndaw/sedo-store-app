# JWT Auth App - Quick Start with Docker Compose

This project uses Spring Boot (Kotlin) and MongoDB (via Docker Compose).

## Prerequisites

- Docker & Docker Compose
- Java 17+
- Gradle (or use the included wrapper)

## 1. Start MongoDB with Docker Compose

```bash
docker compose -f docker-compose.dev.yml up -d
```

This will start MongoDB in a container (service name: `jwt-auth-mongodb`).

## 2. Start the Spring Boot Application

```bash
./gradlew bootRun
```

Or with the development profile:

```bash
./gradlew bootRun -Dspring.profiles.active=dev
```

## MongoDB Connection Details

- **Host**: localhost
- **Port**: 27017
- **Database**: jwtauthdb
- **Admin user**: admin
- **Admin password**: password123

## Useful Commands

```bash
# View MongoDB logs
docker logs jwt-auth-mongodb

# Connect to MongoDB shell
docker exec -it jwt-auth-mongodb mongosh -u admin -p password123

# Stop MongoDB
docker compose -f docker-compose.dev.yml down
```
