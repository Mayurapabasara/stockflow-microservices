# StockFlow Inventory Service

The inventory service owns warehouse stock, stock reservations, transfers, and the immutable movement history for StockFlow. It maintains `quantity`, `reservedQuantity`, and derived `availableQuantity` separately so a reservation cannot oversell stock.

## Run locally

```bash
createdb inventory_db
export DB_URL=jdbc:postgresql://localhost:5432/inventory_db
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
./mvnw spring-boot:run
```

The service listens on <http://localhost:8083>. Flyway creates warehouses, inventory, movements, and reservation tables at startup.

## API

All endpoints require a bearer token. Warehouse administration, adjustments, transfers, and reservation changes require the `ADMIN` role.

| Endpoint | Purpose |
| --- | --- |
| `GET /api/inventory` | Paginated inventory records |
| `GET /api/inventory/{warehouseId}/products/{productId}` | One warehouse/product balance |
| `/api/inventory/warehouses` | Warehouse CRUD |
| `GET /api/inventory/movements` | Filterable movement history |
| `POST /api/inventory/adjustments` | Signed stock adjustment |
| `POST /api/inventory/transfers` | Warehouse-to-warehouse transfer |
| `POST /api/inventory/reservations` | Reserve available stock |
| `POST /api/inventory/reservations/{id}/release` | Release a reservation |
| `POST /api/inventory/reservations/{id}/consume` | Consume a reservation into a sale movement |

## Kafka integration

The service publishes JSON events to:

- `inventory.stock.updated`
- `inventory.low-stock`

It consumes `product.created`, `product.updated`, `purchase.received`, and `sale.fulfilled`. Purchase and sales messages must include `productId`, `warehouseId`, and a positive `quantity`; `id` is optional and is used as an audit reference.

Set `KAFKA_LISTENER_AUTO_STARTUP=false` to start the API without consuming events. Set `KAFKA_ENABLED=false` to disable topic provisioning in local or test environments.

## Product validation

`PRODUCT_VALIDATION_ENABLED` defaults to `false` so inventory can be operated independently while Kafka catalog events are available. Set it to `true` with `PRODUCT_SERVICE_URL` when direct validation through Product Service is required. The direct call must have service-to-service authentication in deployments where Product Service protects all product APIs.

## Tests

```bash
./mvnw verify
```

Tests use H2 in PostgreSQL compatibility mode and execute all Flyway migrations. Kafka topic creation and listeners are disabled in test configuration.

## Docker

```bash
docker build -t stockflow/inventory-service .
docker run --rm -p 8083:8083 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/inventory_db \
  -e KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  -e JWT_JWK_SET_URI=http://host.docker.internal:8081/.well-known/jwks.json \
  stockflow/inventory-service
```

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8083` | HTTP port |
| `DB_URL` | `jdbc:postgresql://localhost:5432/inventory_db` | PostgreSQL JDBC URL |
| `DB_USERNAME` / `DB_PASSWORD` | `postgres` / `postgres` | Database credentials |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka brokers |
| `JWT_JWK_SET_URI` | `http://localhost:8081/.well-known/jwks.json` | Auth service JWKS endpoint |
| `PRODUCT_VALIDATION_ENABLED` | `false` | Enables direct Product Service validation |
| `PRODUCT_SERVICE_URL` | `http://localhost:8082` | Product Service base URL |
| `CONFIG_SERVER_URL` | `http://localhost:8888` | Optional Config Server URL |
| `EUREKA_DEFAULT_ZONE` | `http://localhost:8761/eureka/` | Eureka registry URL |

