CREATE TABLE responder (
    id BIGSERIAL PRIMARY KEY,      -- BIGSERIAL for auto-incrementing ID
    name VARCHAR(255) NOT NULL,     -- Name of the responder
    company_name VARCHAR(255) NOT NULL,  -- Name of the company
    user_id BIGINT NOT NULL         -- User ID (assuming this is a foreign key)
);

-- Optional: Add an index on the user_id if this is frequently queried
CREATE INDEX idx_responder_user_id ON responder(user_id);