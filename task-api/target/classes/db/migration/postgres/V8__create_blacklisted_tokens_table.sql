-- Create blacklisted_tokens table for JWT token blacklisting
CREATE TABLE blacklisted_tokens (
    id BIGSERIAL PRIMARY KEY,
    jti VARCHAR(255) NOT NULL UNIQUE,
    token_type VARCHAR(20) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    blacklisted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reason VARCHAR(100)
);

-- Create indexes for performance
CREATE INDEX idx_blacklisted_token_jti ON blacklisted_tokens(jti);
CREATE INDEX idx_blacklisted_token_expiry ON blacklisted_tokens(expiry_date);

-- Add comments
COMMENT ON TABLE blacklisted_tokens IS 'Stores blacklisted JWT tokens to prevent reuse';
COMMENT ON COLUMN blacklisted_tokens.jti IS 'JWT ID claim from the token';
COMMENT ON COLUMN blacklisted_tokens.token_type IS 'Type of token: ACCESS or REFRESH';
COMMENT ON COLUMN blacklisted_tokens.expiry_date IS 'When the token expires naturally';
COMMENT ON COLUMN blacklisted_tokens.blacklisted_at IS 'When the token was blacklisted';
COMMENT ON COLUMN blacklisted_tokens.reason IS 'Reason for blacklisting: logout, revoked, etc.';
