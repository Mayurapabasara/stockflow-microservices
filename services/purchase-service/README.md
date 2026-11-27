# StockFlow Purchase Service

Manages purchase orders and immutable goods receipts on port 8085. PostgreSQL stores the order and receipt lines; Flyway creates the schema.

## Run

Create database `stockflow_purchase`, then run `./mvnw spring-boot:run`. Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` for your database (defaults: local PostgreSQL, postgres/postgres). JWT verification uses `JWT_JWK_SET_URI`, defaulting to Auth Service at `http://localhost:8081/.well-known/jwks.json`.

## API

All APIs require a bearer token; write operations require the ADMIN role.

| Method and path | Action |
| --- | --- |
| POST /api/purchases | Create a draft order |
| GET /api/purchases | Paginated orders |
| GET /api/purchases/{id} | Order and lines |
| POST /api/purchases/{id}/submit | Submit a draft |
| POST /api/purchases/{id}/cancel | Cancel a draft or unreceived submitted order |
| POST /api/purchases/receipts | Record goods against submitted order lines |
| GET /api/purchases/receipts | Paginated receipts |
| GET /api/purchases/receipts/{id} | Receipt and lines |

Order creation body:

```json
{
  "supplierId": "00000000-0000-0000-0000-000000000001",
  "warehouseId": "00000000-0000-0000-0000-000000000002",
  "items": [{"productId": "00000000-0000-0000-0000-000000000003", "quantity": 10, "unitPrice": 12.50}]
}
```

Receipt creation body contains `purchaseOrderId` and `items`, each with a `purchaseOrderItemId` from the order response and a positive `quantity`. Partial receipts set PARTIALLY_RECEIVED; the final receipt sets RECEIVED. Order locking serializes concurrent receipt and cancellation attempts. Over-receipts, duplicate lines, and receipt lines from another order are rejected. Orders and receipts are retained for audit.

## Integration and configuration

- `SERVER_PORT`: 8085.
- `CONFIG_SERVER_URL`: optional Config Server, http://localhost:8888.
- `EUREKA_DEFAULT_ZONE`: http://localhost:8761/eureka/.
- `PRODUCT_VALIDATION_ENABLED` and `SUPPLIER_VALIDATION_ENABLED`: false by default; set true to check referenced records through their APIs.
- `PRODUCT_SERVICE_URL`: http://localhost:8082; `SUPPLIER_SERVICE_URL`: http://localhost:8084. Validation calls forward the current user's bearer token.
- `KAFKA_BOOTSTRAP_SERVERS`: localhost:9092; `KAFKA_ENABLED=false` disables topic creation and publishing.
- `PURCHASE_RECEIVED_TOPIC`: purchase.received.

Each received line emits a GoodsReceivedEvent after database commit with `id`, `receiptId`, `purchaseOrderId`, `productId`, `warehouseId`, `quantity`, and `occurredAt`, matching Inventory Service's consumer. Publishing currently logs failures without a durable outbox or replay mechanism. Delivery is not atomic with the database commit, and the existing inventory consumer does not deduplicate events; reliable production delivery needs an outbox and consumer idempotency. Warehouse IDs are stored as external references and are not validated by this module.

## Verify and Docker

Run `./mvnw verify`. Tests execute migrations against H2 in PostgreSQL mode and check receipt lifecycle and rollback rules. Build the container with `docker build -t stockflow/purchase-service .`; expose port 8085 and supply the database, Kafka, registry, and JWKS addresses for your environment.
