CREATE TABLE purchase_orders (
    id UUID PRIMARY KEY,
    supplier_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE purchase_order_items (
    id UUID PRIMARY KEY,
    purchase_order_id UUID NOT NULL REFERENCES purchase_orders(id),
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL CHECK (quantity > 0),
    received_quantity BIGINT NOT NULL DEFAULT 0 CHECK (received_quantity >= 0 AND received_quantity <= quantity),
    unit_price NUMERIC(19,2) NOT NULL CHECK (unit_price >= 0),
    CONSTRAINT uk_purchase_order_product UNIQUE (purchase_order_id, product_id)
);
CREATE INDEX idx_purchase_orders_supplier ON purchase_orders(supplier_id);
CREATE INDEX idx_purchase_orders_status ON purchase_orders(status);
