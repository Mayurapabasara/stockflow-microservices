# StockFlow Notification Service

Consumes inventory low-stock, completed sale, and goods receipt events; stores notifications and sends HTML emails to a configured operations mailbox. Port 8087.

## Run

Create PostgreSQL database `notification_db` and run `./mvnw spring-boot:run`. Flyway creates the schema. Configure `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` (defaults: local PostgreSQL, postgres/postgres). Authentication uses `JWT_JWK_SET_URI`, default http://localhost:8081/.well-known/jwks.json.

Email delivery is disabled by default. Set `MAIL_ENABLED=true`, `MAIL_HOST`, `MAIL_PORT`, `MAIL_FROM`, and `NOTIFICATION_RECIPIENT` to enable it. Current events do not contain customer addresses, so order updates go to operations, once per received order line.

## API

All endpoints require an ADMIN bearer token.

| Method and path | Purpose |
| --- | --- |
| GET /api/notifications | Paginated notification history |
| GET /api/notifications/{id} | Details, status, attempts, and timestamps |
| POST /api/notifications/{id}/retry | Requeue a FAILED email |

States are PENDING, SENT, and FAILED. A scheduled worker processes up to 20 pending records per pass; failed deliveries require explicit retry. Notifications remain pending when delivery is disabled. Templates escape event text.

## Kafka and delivery

| Topic | Event |
| --- | --- |
| inventory.low-stock | Product, warehouse, availableQuantity, reorderPoint, occurredAt |
| sale.fulfilled | id, salesOrderId, productId, warehouseId, quantity, occurredAt |
| purchase.received | id, receiptId, purchaseOrderId, productId, warehouseId, quantity, occurredAt |

The consumers validate event fields and commit notification records before acknowledging Kafka records. Sales and purchase records deduplicate by event ID; low-stock records deduplicate by a hash of topic/partition/offset because no event ID is supplied. Re-publishing low-stock data at a new offset creates a new alert. Unique-key conflicts from concurrent delivery can be retried safely.

Transient consumer errors retry twice; invalid events go directly to `<source-topic>.DLT`. The recovery publisher waits for successful DLT delivery; operators must inspect and replay dead letters. Default source and DLT topics use three partitions and one replica; provision appropriate topics for production, matching DLT partition counts to source topics.

The email worker locks each notification while sending to avoid concurrent sends by separate instances. SMTP success followed by a database/process failure can still produce duplicate mail on retry. SENT means SMTP accepted the message, not confirmed inbox delivery; exactly-once email delivery is not guaranteed.

## Configuration

- `SERVER_PORT`: 8087.
- `KAFKA_BOOTSTRAP_SERVERS`: localhost:9092; `KAFKA_ENABLED=false` disables consumers and topic provisioning.
- `KAFKA_LISTENER_AUTO_STARTUP=false`: pause listeners independently.
- `NOTIFICATION_GROUP`: notification-service.
- `LOW_STOCK_TOPIC`, `SALE_COMPLETED_TOPIC`, `PURCHASE_RECEIVED_TOPIC`: override topic names above.
- `MAIL_HOST` / `MAIL_PORT`: localhost / 1025.
- `MAIL_USERNAME` / `MAIL_PASSWORD`: empty; `MAIL_SMTP_AUTH` / `MAIL_STARTTLS_ENABLED`: false. Configure these for your SMTP provider.
- `MAIL_FROM`: notifications@stockflow.test; `NOTIFICATION_RECIPIENT`: operations@stockflow.test.
- `MAIL_DISPATCH_DELAY_MS`: 5000. SMTP connection, read, and write timeouts are five seconds.
- `CONFIG_SERVER_URL`: optional Config Server at http://localhost:8888.
- `EUREKA_DEFAULT_ZONE`: http://localhost:8761/eureka/.

## Verify and Docker

Run `./mvnw verify`. Tests use H2 in PostgreSQL compatibility mode and mocked mail delivery; no emails are sent. They exercise event parsing, deduplication, delivery status, retry, and template escaping. They do not run a live Kafka broker or SMTP server.

Build with `docker build -t stockflow/notification-service .`; expose port 8087 and supply database, Kafka, registry, JWKS, and SMTP settings for your environment.
