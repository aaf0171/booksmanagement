CREATE TABLE IF NOT EXISTS roles (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS login_roles (
    login_id BIGINT NOT NULL,
    role_id  BIGINT NOT NULL,
    PRIMARY KEY (login_id, role_id),
    CONSTRAINT fk_login_roles_login
        FOREIGN KEY (login_id)
        REFERENCES logins(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_login_roles_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_login_roles_role
    ON login_roles(role_id);

INSERT INTO roles (id, name) VALUES
    (1, 'BORROWER'),
    (2, 'LIBRARIAN'),
    (3, 'ADMIN');
