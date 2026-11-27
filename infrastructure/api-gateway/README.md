# StockFlow API Gateway

The API gateway is the public entry point for StockFlow. It authenticates JWTs, applies CORS and correlation IDs, logs requests, and routes traffic to services registered with Eureka.

## Requirements

- Java 21
- The StockFlow discovery service
- The StockFlow authentication service or another compatible JWKS provider

## Run locally

```bash
./mvnw spring-boot:run
```

The gateway listens on <http://localhost:8080>. Its health endpoint is available at <http://localhost:8080/actuator/health>.

## Routes

| Path | Service |
| --- | --- |
| `/api/auth/**` | `auth-service` |
| `/api/products/**` | `product-service` |
| `/api/inventory/**` | `inventory-service` |
| `/api/sales/**` | `sales-service` |
| `/api/purchases/**` | `purchase-service` |
| `/api/suppliers/**` | `supplier-service` |
| `/api/reports/**` | `reporting-service` |
| `/api/notifications/**` | `notification-service` |

The authentication route and actuator health/info endpoints are public. Other routes require a valid bearer token.

## Run tests

```bash
./mvnw verify
```

## Build and run with Docker

```bash
docker build -t stockflow/api-gateway .
docker run --rm -p 8080:8080 \
  -e EUREKA_DEFAULT_ZONE=http://host.docker.internal:8761/eureka/ \
  -e JWT_JWK_SET_URI=http://host.docker.internal:8081/.well-known/jwks.json \
  stockflow/api-gateway
```

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8080` | Gateway HTTP port |
| `CONFIG_SERVER_URL` | `http://localhost:8888` | Optional Spring Config Server URL |
| `EUREKA_DEFAULT_ZONE` | `http://localhost:8761/eureka/` | Eureka registry URL |
| `JWT_JWK_SET_URI` | `http://localhost:8081/.well-known/jwks.json` | JWT signing-key endpoint |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Comma-separated allowed origin patterns |

