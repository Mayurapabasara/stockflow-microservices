CREATE TABLE suppliers (
    id UUID PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    contact_name VARCHAR(120),
    email VARCHAR(254),
    phone VARCHAR(40),
    address VARCHAR(500),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_suppliers_status ON suppliers(status);
