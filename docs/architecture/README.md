# Architecture decisions

## Service boundaries

Each domain module is an independently buildable Spring Boot application. The root Maven project aggregates modules and centralizes compatible Spring Boot and Spring Cloud versions. Product data belongs to Product Service; additional services should own their own database/schema rather than querying another service's tables.

## Discovery and routing

Discovery Service runs Netflix Eureka. The API Gateway routes `/api/products/**` to the registered `product-service`; clients should use the gateway rather than hard-coding service addresses. Other routes should be added alongside their services as the domain APIs are implemented.

## Asynchronous communication

Kafka is included for local development. No message schema or event flow is established in this increment. Define versioned event contracts, idempotency, retry/dead-letter behavior, and ownership before implementing producers and consumers.

## Data and API

Product Service uses JPA with Flyway-owned schema migrations, bean validation, and RFC 9457-style problem details. REST endpoints use UUID resource identifiers, JSON payloads, and standard HTTP status codes. The current service has no authentication; do not expose it to untrusted networks.

## Deployment

Local dependencies are in Docker Compose. The Kubernetes manifest is illustrative and references pre-created ConfigMap/Secret values. Azure provisioning and production hardening are intentionally outside this first increment.
