# StockFlow Product Service

The product service owns catalog products, categories, and brands. It persists catalog data in PostgreSQL and publishes product-created and product-updated events to Kafka.

## Run locally

```bash
createdb stockflow_product
export DB_URL=jdbc:postgresql://localhost:5432/stockflow_product
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
./mvnw spring-boot:run
```

The service listens on <http://localhost:8082>. Swagger UI is available at <http://localhost:8082/swagger-ui.html>.

## APIs

- `/api/products` — paginated product CRUD
- `/api/categories` — category CRUD
- `/api/brands` — brand CRUD

All API operations require a bearer token. Product, category, and brand writes require the `ADMIN` role.

## Events

- `product.created`
- `product.updated`

Topic names can be changed through `PRODUCT_CREATED_TOPIC` and `PRODUCT_UPDATED_TOPIC`.

## Tests

```bash
./mvnw verify
```

The test suite covers controller delegation, service behavior, repository queries, migrations, and an end-to-end service flow using H2 in PostgreSQL mode.

## Docker

```bash
docker build -t stockflow/product-service .
docker run --rm -p 8082:8082 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/stockflow_product \
  -e KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  -e JWT_JWK_SET_URI=http://host.docker.internal:8081/.well-known/jwks.json \
  stockflow/product-service
```

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8082` | HTTP port |
| `DB_URL` | `jdbc:postgresql://localhost:5432/stockflow_product` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `postgres` | Database password |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka brokers |
| `JWT_JWK_SET_URI` | `http://localhost:8081/.well-known/jwks.json` | Authentication-service JWKS endpoint |
| `CONFIG_SERVER_URL` | `http://localhost:8888` | Optional Config Server URL |
| `EUREKA_DEFAULT_ZONE` | `http://localhost:8761/eureka/` | Eureka registry URL |

