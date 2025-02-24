CREATE TABLE client_vehicles (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    FOREIGN KEY (client_id) REFERENCES client(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id)
);



