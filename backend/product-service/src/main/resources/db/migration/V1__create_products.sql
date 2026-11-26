CREATE TABLE products (
    id BINARY(16) NOT NULL PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    sku VARCHAR(64) NOT NULL UNIQUE,
    description VARCHAR(2000),
    price DECIMAL(12, 2) NOT NULL,
    quantity INT NOT NULL,
    category VARCHAR(120) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL
);
