# StockFlow Authentication Service

The authentication service manages users and roles, issues RSA-signed access tokens, rotates refresh tokens, and publishes its public JSON Web Key Set (JWKS) for the API gateway.

## Requirements

- Java 21
- PostgreSQL
- Optional: Config Server and Eureka

## Run locally

Create the database and provide credentials if they differ from the development defaults:

```bash
createdb auth_db
export DB_URL=jdbc:postgresql://localhost:5432/auth_db
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
./mvnw spring-boot:run
```

The service listens on <http://localhost:8081>. Flyway creates the authentication tables and the `USER` and `ADMIN` roles automatically.

## API

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Public | Register and issue tokens |
| `POST` | `/api/auth/login` | Public | Authenticate and issue tokens |
| `POST` | `/api/auth/refresh` | Public | Rotate a refresh token |
| `POST` | `/api/auth/logout` | Public | Revoke a refresh token |
| `GET` | `/.well-known/jwks.json` | Public | Publish the JWT verification key |
| `GET` | `/api/users/me` | Bearer token | Return the current user |
| `GET` | `/api/users/{id}` | `ADMIN` | Return a user by ID |

## JWT keys

If no keys are configured, the service generates an ephemeral 2048-bit RSA key pair at startup. That is convenient for local development, but existing access tokens become invalid after a restart.

For stable environments, provide an RSA public key in X.509 format and private key in PKCS#8 format. Values may be PEM text or Base64-encoded DER:

```bash
export JWT_PUBLIC_KEY='<public key>'
export JWT_PRIVATE_KEY='<private key>'
```

Only the public key is returned by the JWKS endpoint. Refresh tokens are stored as SHA-256 hashes and are rotated whenever they are used.

## Run tests

```bash
./mvnw verify
```

Tests use an in-memory H2 database and do not require PostgreSQL, Eureka, or Config Server.

## Docker

```bash
docker build -t stockflow/auth-service .
docker run --rm -p 8081:8081 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/auth_db \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  stockflow/auth-service
```

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8081` | HTTP port |
| `DB_URL` | `jdbc:postgresql://localhost:5432/auth_db` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `postgres` | Database password |
| `CONFIG_SERVER_URL` | `http://localhost:8888` | Optional Config Server URL |
| `EUREKA_DEFAULT_ZONE` | `http://localhost:8761/eureka/` | Eureka registry URL |
| `JWT_ISSUER` | `stockflow-auth-service` | Access-token issuer |
| `JWT_KEY_ID` | `stockflow-auth` | JWKS key identifier |
| `JWT_ACCESS_TOKEN_TTL` | `PT15M` | Access-token lifetime |
| `JWT_REFRESH_TOKEN_TTL` | `P7D` | Refresh-token lifetime |

