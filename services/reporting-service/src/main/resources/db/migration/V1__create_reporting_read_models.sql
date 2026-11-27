CREATE TABLE sales_summaries (
    id UUID PRIMARY KEY,
    sales_order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    quantity BIGINT NOT NULL CHECK (quantity > 0),
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX idx_sales_summary_time ON sales_summaries(occurred_at);
CREATE INDEX idx_sales_summary_product_warehouse ON sales_summaries(product_id,warehouse_id);

CREATE TABLE purchase_summaries (
    id UUID PRIMARY KEY,
    receipt_id UUID NOT NULL,
    purchase_order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    quantity BIGINT NOT NULL CHECK (quantity > 0),
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX idx_purchase_summary_time ON purchase_summaries(occurred_at);
CREATE INDEX idx_purchase_summary_product_warehouse ON purchase_summaries(product_id,warehouse_id);

CREATE TABLE inventory_snapshots (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    quantity BIGINT NOT NULL CHECK (quantity >= 0),
    reserved_quantity BIGINT NOT NULL CHECK (reserved_quantity >= 0),
    available_quantity BIGINT NOT NULL CHECK (available_quantity >= 0),
    movement_type VARCHAR(32) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_inventory_snapshot_product_warehouse UNIQUE(product_id,warehouse_id),
    CONSTRAINT ck_inventory_snapshot_balance CHECK (available_quantity = quantity - reserved_quantity)
);
CREATE INDEX idx_inventory_snapshot_updated ON inventory_snapshots(occurred_at);
