-- V2__create_refresh_tokens_table.sql
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id    BIGINT       NOT NULL,
    token_hash  VARCHAR(64)  NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_login
        FOREIGN KEY (login_id)
        REFERENCES logins(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_token_hash
    ON refresh_tokens(token_hash);

CREATE INDEX IF NOT EXISTS idx_refresh_token_login
    ON refresh_tokens(login_id);
