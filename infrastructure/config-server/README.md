# StockFlow Config Server

The Config Server centralizes configuration for StockFlow infrastructure and business services. By default, it reads configuration from the repository-level `config-repository` directory using Spring Cloud Config's native backend.

## Requirements

- Java 21
- The repository's `config-repository/` directory

## Run locally

```bash
./mvnw spring-boot:run
```

The server listens on <http://localhost:8888>. Configuration can be queried using Spring Cloud Config's standard endpoint format:

```text
http://localhost:8888/{application}/{profile}[/{label}]
```

For example:

```bash
curl http://localhost:8888/api-gateway/default
```

## Run tests

```bash
./mvnw verify
```

## Build and run with Docker

Build the image from this directory, then mount the configuration repository read-only:

```bash
docker build -t stockflow/config-server .
docker run --rm -p 8888:8888 \
  -v "$(pwd)/../../config-repository:/config-repository:ro" \
  stockflow/config-server
```

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8888` | Config Server HTTP port |
| `SPRING_PROFILES_ACTIVE` | `native` | Selects the Config Server backend profile |
| `CONFIG_REPOSITORY_LOCATION` | `file:../../config-repository` locally | Native configuration repository location |
| `EUREKA_DEFAULT_ZONE` | `http://localhost:8761/eureka/` | Eureka registry URL |
| `EUREKA_PREFER_IP_ADDRESS` | `true` | Advertises the service by IP address |

The Docker image sets `CONFIG_REPOSITORY_LOCATION` to `file:/config-repository` automatically.

