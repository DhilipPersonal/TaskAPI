ALTER TABLE taskapp.users
ADD COLUMN failed_login_attempts INT NOT NULL DEFAULT 0,
ADD COLUMN lockout_until TIMESTAMP NULL;
