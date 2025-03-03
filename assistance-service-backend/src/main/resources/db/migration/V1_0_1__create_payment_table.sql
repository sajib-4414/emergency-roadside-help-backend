CREATE TABLE payment (
    id BIGSERIAL PRIMARY KEY,
    assistance_id BIGINT NOT NULL REFERENCES assistance(id) ON DELETE CASCADE,
    payment_method VARCHAR(10),
    paid_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    transaction_id TEXT
);
