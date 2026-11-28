CREATE TABLE customers (
    id UUID PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    email VARCHAR(254),
    phone VARCHAR(40),
    address VARCHAR(500),
    active BOOLEAN NOT NULL
);
CREATE TABLE sales_orders (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES customers(id),
    warehouse_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE sales_order_items (
    id UUID PRIMARY KEY,
    sales_order_id UUID NOT NULL REFERENCES sales_orders(id),
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(19,2) NOT NULL CHECK (unit_price >= 0),
    CONSTRAINT uk_sales_order_product UNIQUE (sales_order_id, product_id)
);
CREATE INDEX idx_sales_orders_customer ON sales_orders(customer_id);
CREATE INDEX idx_sales_orders_status ON sales_orders(status);
