# StockFlow frontend

React and TypeScript frontend built with Vite.

## Development

```sh
npm install
npm run dev
```

Run `npm run build` to type-check and build the application, and `npm run lint` to check the source.

## Structure

- `src/features/` groups application code by business domain; auth and products include folders for components, pages, API calls, hooks, and types.
- `src/components/` holds shared UI, layout, and chart components.
- `src/api/`, `context/`, `hooks/`, `providers/`, and `routes/` are reserved for shared application infrastructure.
- `src/assets/`, `constants/`, `types/`, and `utils/` hold shared resources and helpers.
- `tests/` is reserved for tests.

The named feature and infrastructure files are placeholders for future implementation. The existing Vite starter screen remains the application entry point. Add the appropriate dependencies and configure API URLs, authentication, and routing when implementing those features.

## Container

Build with `docker build -t stockflow-frontend .` and run with `docker run -p 8080:80 stockflow-frontend`. Nginx serves the built application and falls back to `index.html` for client-side routes.
