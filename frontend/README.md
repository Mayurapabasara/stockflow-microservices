# StockFlow frontend

The StockFlow operations console is a React and TypeScript single-page application. It includes authentication, dashboard metrics, product and catalog maintenance, inventory adjustments, suppliers, purchases and receipts, sales and customers, invoices and payments, reports, and notifications.

## Development

The frontend calls the API Gateway, not individual services. Copy `.env.example` to `.env` if the gateway is not running at the default URL.

```sh
npm install
npm run dev
```

The default development URL is `http://localhost:5173`; the default API URL is `http://localhost:8080`. Run `npm run build` for a type-checked production build and `npm run lint` for static checks.

Authentication tokens are stored in `sessionStorage` and refreshed once after an unauthorized response. Closing the browser session clears them. Administrative actions are hidden or route-protected for non-admin users, while the backend remains the source of truth for authorization.

## Backend dependencies

Start PostgreSQL, Kafka, the config/discovery infrastructure, the eight domain services, and the API Gateway. Each service accepts `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`; its checked-in default targets the matching local database (`auth_db`, `product_db`, `inventory_db`, `supplier_db`, `purchase_db`, `sales_db`, `notification_db`, or `report_db`).

## Container

Build and run the static Nginx image:

```sh
docker build --build-arg VITE_API_BASE_URL=http://localhost:8080 -t stockflow-frontend .
docker run --rm -p 3000:80 stockflow-frontend
```

`VITE_API_BASE_URL` is compiled into the browser bundle during the image build. Nginx falls back to `index.html` for client-side routes.
