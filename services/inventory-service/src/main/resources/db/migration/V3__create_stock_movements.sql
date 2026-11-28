CREATE TABLE stock_movements (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    warehouse_id UUID NOT NULL REFERENCES warehouses(id) ON DELETE RESTRICT,
    type VARCHAR(32) NOT NULL,
    quantity_delta BIGINT NOT NULL,
    reserved_quantity_delta BIGINT NOT NULL,
    reference_id VARCHAR(100),
    reason VARCHAR(500),
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_stock_movements_product_id ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_warehouse_id ON stock_movements(warehouse_id);
CREATE INDEX idx_stock_movements_occurred_at ON stock_movements(occurred_at);

