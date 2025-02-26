ALTER TABLE client
ADD COLUMN user_id BIGINT;

ALTER TABLE client
ADD CONSTRAINT fk_user
FOREIGN KEY (user_id)
REFERENCES users(id);





