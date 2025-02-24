-- V3__Create_booking_request_table.sql
CREATE TABLE booking_request (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    date_created TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    vehicle_id BIGINT NOT NULL,
    description TEXT,
    priority VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    service_type VARCHAR(50) NOT NULL,
    FOREIGN KEY (client_id) REFERENCES client(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id)
);




