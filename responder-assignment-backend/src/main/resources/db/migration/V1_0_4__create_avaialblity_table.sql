CREATE TABLE responder_availability (
    id BIGSERIAL PRIMARY KEY,
    responder_id BIGINT NOT NULL,
    day_of_weeks VARCHAR(255) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CONSTRAINT fk_responder
        FOREIGN KEY(responder_id)
        REFERENCES responder(id)
);

CREATE INDEX idx_responder_availability_responder_id ON responder_availability(responder_id);