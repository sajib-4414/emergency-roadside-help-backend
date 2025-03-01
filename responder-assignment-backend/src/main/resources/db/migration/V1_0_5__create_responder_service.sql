CREATE TABLE responder_service (
    id BIGSERIAL PRIMARY KEY,
    responder_id BIGINT NOT NULL,
    service_type VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    notes TEXT,
    CONSTRAINT fk_responder
        FOREIGN KEY(responder_id)
        REFERENCES responder(id)
);

CREATE INDEX idx_responder_service_responder_id ON responder_service(responder_id);