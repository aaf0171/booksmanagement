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
