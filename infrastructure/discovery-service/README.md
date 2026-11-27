# StockFlow Discovery Service

The discovery service provides a Netflix Eureka registry for StockFlow's infrastructure and business microservices.

## Requirements

- Java 21
- Docker, if running the container image

## Run locally

```bash
./mvnw spring-boot:run
```

The Eureka dashboard is available at <http://localhost:8761> and the health endpoint at <http://localhost:8761/actuator/health>.

## Run tests

```bash
./mvnw verify
```

## Build and run with Docker

```bash
docker build -t stockflow/discovery-service .
docker run --rm -p 8761:8761 stockflow/discovery-service
```

## Configuration

The service supports the following environment variables:

| Variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8761` | HTTP port exposed by the service |
| `EUREKA_HOSTNAME` | `localhost` | Hostname advertised by Eureka |
| `EUREKA_DEFAULT_ZONE` | `http://<hostname>:<port>/eureka/` | Eureka service URL |
| `EUREKA_SELF_PRESERVATION` | `true` | Enables Eureka self-preservation mode |

