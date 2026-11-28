# StockFlow Supplier Service

The supplier service owns supplier records and their product sourcing terms: supplier SKU, unit price, lead time, and activation status. A supplier can have one sourcing record for each product.

## Run locally

```bash
createdb supplier_db
export DB_URL=jdbc:postgresql://localhost:5432/supplier_db
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
./mvnw spring-boot:run
```

The service listens on <http://localhost:8084>. Flyway creates the supplier and supplier-product tables at startup.

## API

All supplier APIs require a bearer token. Creating, changing, and deleting suppliers requires the `ADMIN` role.

| Endpoint | Purpose |
| --- | --- |
| `GET /api/suppliers` | Paginated supplier list |
| `GET /api/suppliers/{id}` | Supplier including sourcing terms |
| `POST /api/suppliers` | Create a supplier and its product terms |
| `PUT /api/suppliers/{id}` | Replace supplier details and product terms |
| `DELETE /api/suppliers/{id}` | Delete a supplier |

## Product validation

`PRODUCT_VALIDATION_ENABLED` is `false` by default, allowing supplier data to be loaded independently. Set it to `true` with `PRODUCT_SERVICE_URL` to validate each referenced product through Product Service. Deployments with protected Product Service endpoints must provide suitable service-to-service authentication for that call.

## Tests

```bash
./mvnw verify
```

Tests use H2 in PostgreSQL compatibility mode and execute the Flyway migrations.

## Docker

```bash
docker build -t stockflow/supplier-service .
docker run --rm -p 8084:8084 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/supplier_db \
  -e JWT_JWK_SET_URI=http://host.docker.internal:8081/.well-known/jwks.json \
  stockflow/supplier-service
```

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8084` | HTTP port |
| `DB_URL` | `jdbc:postgresql://localhost:5432/supplier_db` | PostgreSQL JDBC URL |
| `DB_USERNAME` / `DB_PASSWORD` | `postgres` / `postgres` | Database credentials |
| `JWT_JWK_SET_URI` | `http://localhost:8081/.well-known/jwks.json` | Auth service JWKS endpoint |
| `PRODUCT_VALIDATION_ENABLED` | `false` | Enables direct Product Service validation |
| `PRODUCT_SERVICE_URL` | `http://localhost:8082` | Product Service base URL |
| `CONFIG_SERVER_URL` | `http://localhost:8888` | Optional Config Server URL |
| `EUREKA_DEFAULT_ZONE` | `http://localhost:8761/eureka/` | Eureka registry URL |
