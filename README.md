# auth-service

CLI-based authentication service — Java 21 (Compatability Issues), Spring Boot 3.4, PostgreSQL.

## Requirements
- Java 21
- Docker & Docker Compose

## Build

    ./gradlew build

## Run tests

    ./gradlew test

## Run locally

    ./gradlew bootRun

## Deploy via Podman Compose

    podman compose up --build

## Interactive CLI

When the app starts a Spring Shell prompt is available:

    shell:> register --username alice --password secret
    Registered: alice

    shell:> login --username alice --password secret
    Login successful

## REST API

  GET  /api/auth/health (Healthchecks)
  POST /api/auth/register   { "username": "x", "password": "y", "email": "z","tel-nr": "?"}
  POST /api/auth/login      { "username": "x", "password": "y", "email": "z","tel-nr": "?"}
  
  
## Access towards Utility

  http://localhost:8080/swagger-ui/index.html#/ for Testing Methods
  http://localhost:2113/ui/cluster KurrentDB for Event Checks


