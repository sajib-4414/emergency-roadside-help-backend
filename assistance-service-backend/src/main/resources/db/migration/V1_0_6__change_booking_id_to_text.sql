ALTER TABLE assistance
    ALTER COLUMN booking_id TYPE TEXT
    USING booking_id::text;



