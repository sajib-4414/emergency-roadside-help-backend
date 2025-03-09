ALTER TABLE assignment
ALTER COLUMN booking_id TYPE VARCHAR(255) USING booking_id::TEXT;