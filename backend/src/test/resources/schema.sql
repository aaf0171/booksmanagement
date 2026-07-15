CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    isbn VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS borrowers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    surname VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS loans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    borrower_id BIGINT NOT NULL,
    loan_date DATE,
    loan_period_days INT NOT NULL DEFAULT 14,
    due_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    subtitle VARCHAR(255),
    document_type VARCHAR(20),
    isbn VARCHAR(20),
    publisher VARCHAR(255),
    publication_year INT,
    language VARCHAR(50),
    description TEXT,
    cover_url VARCHAR(500),
    created_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_documents_search ON documents(title, subtitle, description, publisher);

CREATE TABLE IF NOT EXISTS authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(100),
    lastname VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS document_authors (
    document_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    PRIMARY KEY (document_id, author_id)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    barcode VARCHAR(50) UNIQUE,
    `status` VARCHAR(20),
    acquisition_date DATE,
    location VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS logins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS activation_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expires_at DATETIME NOT NULL,
    used_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_activation_token_login
        FOREIGN KEY (login_id) REFERENCES logins(id)
        ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_activation_token_hash
    ON activation_tokens(token_hash);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id    BIGINT       NOT NULL,
    token_hash  VARCHAR(64)  NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_login
        FOREIGN KEY (login_id) REFERENCES logins(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_token_hash
    ON refresh_tokens(token_hash);

CREATE INDEX IF NOT EXISTS idx_refresh_token_login
    ON refresh_tokens(login_id);
