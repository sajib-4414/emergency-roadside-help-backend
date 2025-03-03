CREATE TABLE assistance_items (
    id BIGSERIAL PRIMARY KEY,
    assistance_id BIGINT NOT NULL REFERENCES assistance(id) ON DELETE CASCADE,
    item_name TEXT NOT NULL,
    quantity INT,
    charge DOUBLE PRECISION NOT NULL,
    description TEXT
);

