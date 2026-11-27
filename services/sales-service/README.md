# StockFlow Sales Service

Owns customers, draft sales orders, invoices, and payment records. Listens on port 8086. All APIs require a bearer token; writes require the ADMIN role.

## Run

Create PostgreSQL database `stockflow_sales` and run `./mvnw spring-boot:run`. Flyway creates the tables. Configure `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (defaults: local PostgreSQL, postgres/postgres), `JWT_JWK_SET_URI` (Auth Service at http://localhost:8081/.well-known/jwks.json), and `KAFKA_BOOTSTRAP_SERVERS` (localhost:9092).

## API

| Method and path | Action |
| --- | --- |
| POST /api/sales/customers | Create customer |
| GET /api/sales/customers | Paginated customers |
| GET /api/sales/customers/{id} | Customer details |
| PUT /api/sales/customers/{id} | Update details or deactivate customer |
| POST /api/sales | Create draft order |
| GET /api/sales | Paginated orders |
| GET /api/sales/{id} | Order details |
| POST /api/sales/{id}/complete | Complete draft and create invoice |
| POST /api/sales/{id}/cancel | Cancel draft |
| GET /api/sales/invoices | Paginated invoices |
| GET /api/sales/invoices/{id} | Invoice, payments, and balance |
| GET /api/sales/invoices/orders/{orderId} | Invoice for order |
| POST /api/sales/invoices/{id}/payments | Record payment |

Customer requests include `name`, optional `email`, `phone`, `address`, and required `active`. Order requests include `customerId`, `warehouseId`, and `items` with `productId`, positive `quantity`, and nonnegative `unitPrice`. Payment requests include positive `amount`, `method` (CASH, CARD, BANK_TRANSFER), and optional `reference`.

Completed and cancelled orders cannot change state. Invoices are issued once on completion. Payments lock the invoice and cannot exceed its outstanding balance. Payment entries are bookkeeping records; this module does not charge cards or call a payment provider. Amounts use two decimal places in one deployment currency; tax, refunds, and currency conversion are not implemented.

## Integration

- `PRODUCT_VALIDATION_ENABLED` and `INVENTORY_VALIDATION_ENABLED` default to false. Enable them to check products at creation and available inventory before completion.
- `PRODUCT_SERVICE_URL` defaults to http://localhost:8082; `INVENTORY_SERVICE_URL` defaults to http://localhost:8083. Calls forward the current bearer token.
- `CONFIG_SERVER_URL` defaults to optional Config Server at http://localhost:8888.
- `EUREKA_DEFAULT_ZONE` defaults to http://localhost:8761/eureka/.
- `SERVER_PORT` defaults to 8086; `KAFKA_ENABLED=false` disables topic provisioning and event publication.
- `SALE_COMPLETED_TOPIC` defaults to `sale.fulfilled`, matching Inventory Service; `SALE_CANCELLED_TOPIC` defaults to `sale.cancelled`.

Completion publishes one SaleCompletedEvent per order line after database commit, with `id`, `salesOrderId`, `productId`, `warehouseId`, `quantity`, and `occurredAt`. Cancellation publishes a SaleCancelledEvent for the draft; it has no stock movement. The consumer package is reserved until inbound contracts are defined.

Inventory checks do not reserve stock and cannot prevent concurrent sales from exceeding availability before events are handled. Event delivery has no durable outbox/replay, and the existing inventory consumer does not deduplicate or acknowledge fulfillment back to Sales Service. COMPLETED therefore records local completion, not confirmed inventory fulfillment. Reliable production fulfillment needs a reservation/saga flow, durable delivery, and consumer idempotency.

## Verify and Docker

Run `./mvnw verify` for migration and workflow tests using H2 in PostgreSQL compatibility mode. Build with `docker build -t stockflow/sales-service .`, expose port 8086, and supply database, Kafka, registry, and JWKS addresses for your environment.
