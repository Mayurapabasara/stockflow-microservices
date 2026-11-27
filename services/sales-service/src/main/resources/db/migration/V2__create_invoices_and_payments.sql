CREATE TABLE invoices (
    id UUID PRIMARY KEY,
    sales_order_id UUID NOT NULL UNIQUE REFERENCES sales_orders(id),
    total NUMERIC(38,2) NOT NULL CHECK (total >= 0),
    issued_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL REFERENCES invoices(id),
    amount NUMERIC(19,2) NOT NULL CHECK (amount > 0),
    method VARCHAR(32) NOT NULL,
    reference VARCHAR(100),
    paid_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX idx_payments_invoice ON payments(invoice_id);
