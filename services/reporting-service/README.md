# StockFlow Reporting Service

Builds operational read models from StockFlow Kafka events and exposes filtered reports, CSV exports, and dashboard totals on port 8088.

## Report model

The current event contracts contain quantities and identifiers, but no prices. Reports therefore show units sold, units received, and inventory quantities; they do not claim revenue, cost, margin, or valuation.

- `sale.fulfilled` creates immutable sales facts and deduplicates by event ID.
- `purchase.received` creates immutable purchase facts and deduplicates by event ID.
- `inventory.stock.updated` upserts the latest product/warehouse snapshot. Older events are ignored.
- Invalid events go to `<source-topic>.DLT` after non-validation failures retry twice.

## API

All endpoints require a bearer token.

| Endpoint | Result |
| --- | --- |
| GET /api/reports/sales | Paginated sales facts |
| GET /api/reports/purchases | Paginated purchase facts |
| GET /api/reports/inventory | Paginated latest inventory snapshots |
| GET /api/reports/dashboard | Sales/purchase totals for the date range and current inventory totals |
| GET /api/reports/{sales,purchases,inventory}/export | CSV attachment |

Report endpoints accept optional ISO-8601 `from`, `to`, `productId`, and `warehouseId` query parameters. The dashboard accepts `from` and `to`. Inventory date filters select current snapshots by their latest event time, not historical inventory. Exports are capped at 10,000 rows; narrow filters when the result is larger.

## Run and configuration

Create PostgreSQL database `stockflow_reporting`, then run `./mvnw spring-boot:run`. Flyway creates all three read models.

- `SERVER_PORT`: 8088.
- `DB_URL`: jdbc:postgresql://localhost:5432/stockflow_reporting; credentials default to postgres/postgres.
- `JWT_JWK_SET_URI`: http://localhost:8081/.well-known/jwks.json.
- `KAFKA_BOOTSTRAP_SERVERS`: localhost:9092.
- `KAFKA_ENABLED=false`: disables consumers and topic provisioning.
- `KAFKA_LISTENER_AUTO_STARTUP=false`: pauses listeners.
- `REPORTING_GROUP`: reporting-service.
- `SALE_COMPLETED_TOPIC`, `STOCK_UPDATED_TOPIC`, `PURCHASE_RECEIVED_TOPIC`: override the default topics above.
- `CONFIG_SERVER_URL`: optional Config Server at http://localhost:8888.
- `EUREKA_DEFAULT_ZONE`: http://localhost:8761/eureka/.

Source and DLT topics default to three partitions and one replica. Production DLT partition counts must match their source topics. Replaying facts is safe when event IDs are stable. Inventory events have no event ID or sequence number: timestamp ordering prevents older updates from replacing newer snapshots, but equal-timestamp conflicts cannot be ordered reliably. Kafka retention defines whether the database can be rebuilt fully.

## Verify and Docker

Run `./mvnw verify`. Tests use H2 in PostgreSQL mode and exercise migrations, event parsing, deduplication, out-of-order inventory, filtering, dashboard totals, and CSV export. They do not start a live Kafka broker.

Build with `docker build -t stockflow/reporting-service .`; expose port 8088 and provide database, Kafka, registry, and JWKS settings.
