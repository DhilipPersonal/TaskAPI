-- Create blacklisted_tokens table for JWT token blacklisting (H2 version)
CREATE TABLE blacklisted_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    jti VARCHAR(255) NOT NULL UNIQUE,
    token_type VARCHAR(20) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    blacklisted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reason VARCHAR(100)
);

-- Create indexes for performance
CREATE INDEX idx_blacklisted_token_jti ON blacklisted_tokens(jti);
CREATE INDEX idx_blacklisted_token_expiry ON blacklisted_tokens(expiry_date);
