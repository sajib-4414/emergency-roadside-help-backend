CREATE TABLE vehicle (
    id BIGSERIAL PRIMARY KEY,
    make VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    trim VARCHAR(255),
    year INT NOT NULL,
    plate VARCHAR(255) NOT NULL
);


