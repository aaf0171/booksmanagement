CREATE TABLE activation_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id    BIGINT       NOT NULL,
    type        VARCHAR(30)  NOT NULL,
    token_hash  VARCHAR(64)  NOT NULL,
    expires_at  DATETIME     NOT NULL,
    used_at     DATETIME     DEFAULT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_activation_token_login
        FOREIGN KEY (login_id) REFERENCES logins(id)
        ON DELETE CASCADE
);

CREATE UNIQUE INDEX uk_activation_token_hash
    ON activation_tokens(token_hash);
