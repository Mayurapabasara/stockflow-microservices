# StockFlow

StockFlow is an evolving inventory and supply-chain platform for small and medium-sized businesses. This repository is a Maven/React monorepo. The first increment establishes service discovery, API routing, working Product CRUD, and local infrastructure; it is not a complete ERP implementation.

## Architecture

```text
React dashboard ──HTTP──> API Gateway ──> Eureka-registered services
                                       ├── Product Service ──> product MySQL database
                                       └── future services

Services ──events (planned)──> Apache Kafka
```

Services own their data (database-per-service). The initial Product Service uses MySQL and exposes a REST API. Gateway routing uses service IDs resolved through Eureka. Kafka is available locally, but event producers/consumers, authentication, and cross-service workflows are future work.

| Service | Initial status |
| --- | --- |
| API Gateway | Spring Cloud Gateway routes to Product Service through Eureka |
| Discovery Service | Netflix Eureka server |
| Product Service | Validated CRUD API, JPA, Flyway, centralized API errors and tests |
| Auth, Inventory, Supplier, Purchase, Sales, Notification, Reporting | Buildable Spring Boot service shells |

The backend uses Java 21, Spring Boot 3.4, Spring Cloud 2024.0, and Maven. The frontend uses React, Vite, Tailwind CSS, React Router, and Axios. Dependencies are managed centrally in the root Maven POM and frontend package manifest.

## Prerequisites

- JDK 21
- Maven 3.9+
- Node.js 20+ and npm
- Docker with Docker Compose plugin

## Local development

1. Copy `.env.example` to `.env` and set unique local values for every required password. Do not commit `.env`.
2. Start local dependencies:

   ```sh
   docker compose up -d
   ```

3. In separate terminals, start discovery, Product Service, then the gateway:

   ```sh
   mvn -pl backend/discovery-service spring-boot:run
   mvn -pl backend/product-service spring-boot:run
   mvn -pl backend/api-gateway spring-boot:run
   ```

   Services read database credentials and connection details from environment variables. Export the values from `.env` in the shell or use a compatible environment-file loader. Eureka is at <http://localhost:8761>, the gateway at <http://localhost:8080>, and Swagger UI for Product Service at <http://localhost:8082/swagger-ui/index.html>.

4. Start the dashboard:

   ```sh
   cd frontend
   npm ci
   npm run dev
   ```

   Vite serves the dashboard at <http://localhost:5173>. During development, `/api` requests are proxied to the gateway.

### Product API

The gateway exposes the product endpoints under `/api/products`:

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/products` | List products |
| GET | `/api/products/{id}` | Get a product |
| POST | `/api/products` | Create a product |
| PUT | `/api/products/{id}` | Replace a product |
| DELETE | `/api/products/{id}` | Delete a product |

Create/update JSON:

```json
{
  "name": "Widget",
  "sku": "WIDGET-001",
  "description": "Sample item",
  "price": 12.50,
  "quantity": 20,
  "category": "Accessories"
}
```

Names, SKUs and categories are required; price must be positive and quantity cannot be negative. SKUs are unique. Errors use `application/problem+json`.

## Build and test

From the repository root:

```sh
mvn verify
cd frontend && npm ci && npm run build
```

`mvn verify` compiles all service modules and runs Product Service unit tests. The GitHub Actions workflow performs the same backend/frontend checks.

## Infrastructure and deployment

- `docker-compose.yml` runs local MySQL and Kafka dependencies; applications are started from Maven during development.
- `infrastructure/kubernetes/` has a starter Product Service Deployment and Service. Configure a real image and Kubernetes Secret before deployment.
- `infrastructure/azure/README.md` describes the intended Azure Container Apps/AKS, managed database, and messaging direction. Cloud resources are intentionally not provisioned by this starter.
- `.env.example` documents local configuration names and contains no credentials.

## Development roadmap

1. Add authenticated users, JWT and role-based authorization in Auth Service.
2. Expand product/category management and add Flyway-managed data contracts.
3. Implement Inventory movements, reservations, and low-stock events.
4. Add supplier, purchase, and sales workflows with Kafka contracts and idempotent consumers.
5. Add notifications, reporting, and operational dashboards.
6. Add production observability, resilience, Kubernetes deployment automation, and Azure provisioning.

The non-Product services are intentionally shells. There is no production authentication, business logic for those modules, Kafka integration, production-grade observability, or Azure deployment yet.
