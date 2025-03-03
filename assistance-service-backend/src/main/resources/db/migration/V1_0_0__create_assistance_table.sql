CREATE TABLE assistance (
    id BIGSERIAL PRIMARY KEY,
    booking_id  BIGINT NOT NULL,
    service_type VARCHAR(20) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    status VARCHAR(30) NOT NULL,
    responder_id BIGINT NOT NULL,
    location TEXT NOT NULL,
    estimated_arrival_time TIMESTAMP
);