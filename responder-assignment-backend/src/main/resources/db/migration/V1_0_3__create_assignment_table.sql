CREATE TABLE assignment (
    id BIGSERIAL PRIMARY KEY,
    responder_id BIGINT NOT NULL,
    assign_status VARCHAR(255) NOT NULL,
    booking_id BIGINT NOT NULL,
    service_type VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    assignment_notes TEXT,
    CONSTRAINT fk_responder
        FOREIGN KEY(responder_id)
        REFERENCES responder(id)
);

CREATE INDEX idx_assignment_responder_id ON assignment(responder_id);