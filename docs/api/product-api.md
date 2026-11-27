# Product API

Product Service listens on port `8082` by default. Through the API Gateway, use `/api/products`. Direct service access is useful for local debugging; production clients should use the gateway.

## Endpoints

| Method | Path | Success | Description |
| --- | --- | --- | --- |
| `GET` | `/api/products` | `200 OK` | Return all products |
| `GET` | `/api/products/{id}` | `200 OK` | Return one product by UUID |
| `POST` | `/api/products` | `201 Created` | Create a product |
| `PUT` | `/api/products/{id}` | `200 OK` | Replace a product |
| `DELETE` | `/api/products/{id}` | `204 No Content` | Delete a product |

`POST` and `PUT` accept `application/json`:

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

`name`, `sku`, and `category` are required. The name is limited to 160 characters, SKU to 64, category to 120, and description to 2,000. Price must be at least `0.01`; quantity must be zero or greater. SKU must be unique.

Missing products return `404 Not Found`; duplicate SKUs return `409 Conflict`; invalid input returns `400 Bad Request`. Errors use `application/problem+json`. OpenAPI UI is available at `/swagger-ui/index.html` on the Product Service.
