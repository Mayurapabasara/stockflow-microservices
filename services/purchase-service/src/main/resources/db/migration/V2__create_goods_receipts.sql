CREATE TABLE goods_receipts (
    id UUID PRIMARY KEY,
    purchase_order_id UUID NOT NULL REFERENCES purchase_orders(id),
    received_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE goods_receipt_items (
    id UUID PRIMARY KEY,
    goods_receipt_id UUID NOT NULL REFERENCES goods_receipts(id),
    purchase_order_item_id UUID NOT NULL REFERENCES purchase_order_items(id),
    quantity BIGINT NOT NULL CHECK (quantity > 0),
    CONSTRAINT uk_goods_receipt_order_item UNIQUE (goods_receipt_id, purchase_order_item_id)
);
CREATE INDEX idx_goods_receipts_order ON goods_receipts(purchase_order_id);
CREATE INDEX idx_goods_receipt_items_order_item ON goods_receipt_items(purchase_order_item_id);
