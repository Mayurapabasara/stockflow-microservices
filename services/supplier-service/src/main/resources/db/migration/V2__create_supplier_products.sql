CREATE TABLE supplier_products (
    id UUID PRIMARY KEY,
    supplier_id UUID NOT NULL REFERENCES suppliers(id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    supplier_sku VARCHAR(64),
    unit_price NUMERIC(19, 2) NOT NULL CHECK (unit_price >= 0),
    lead_time_days INTEGER NOT NULL CHECK (lead_time_days >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_supplier_products_supplier_product UNIQUE (supplier_id, product_id)
);

CREATE INDEX idx_supplier_products_supplier_id ON supplier_products(supplier_id);
CREATE INDEX idx_supplier_products_product_id ON supplier_products(product_id);
