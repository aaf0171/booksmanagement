-- =========================
-- NOTICE BIBLIOGRAPHIQUE
-- =========================

CREATE TABLE documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    document_type ENUM(
        'BOOK',
        'DVD',
        'GAME',
        'DEVICE',
        'OTHER'
    ) NOT NULL DEFAULT 'BOOK',
    isbn VARCHAR(20),
    publisher VARCHAR(255),
    publication_year INT,
    language VARCHAR(50),
    description TEXT,
    cover_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FULLTEXT INDEX idx_documents_search (
        title,
        subtitle,
        description,
        publisher
    )
) ENGINE=InnoDB;

-- =========================
-- AUTEURS
-- =========================

CREATE TABLE authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(100),
    lastname VARCHAR(100) NOT NULL
) ENGINE=InnoDB;


CREATE TABLE document_authors (
    document_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    PRIMARY KEY (
        document_id,
        author_id
    ),
    FOREIGN KEY (document_id)
        REFERENCES documents(id)
        ON DELETE CASCADE,
    FOREIGN KEY (author_id)
        REFERENCES authors(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;


-- =========================
-- CATALOGAGE : EXEMPLAIRES
-- =========================

CREATE TABLE items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    barcode VARCHAR(50) UNIQUE NOT NULL,
    acquisition_date DATE,
    location VARCHAR(100),
    status ENUM(
        'AVAILABLE',
        'LOANED',
        'LOST',
        'DAMAGED',
        'REPAIR'
    )
    NOT NULL DEFAULT 'AVAILABLE',
    FOREIGN KEY(document_id)
        REFERENCES documents(id)
        ON DELETE RESTRICT,
    INDEX idx_items_document (
        document_id
    )
) ENGINE=InnoDB;

-- =========================
-- EMPRUNTEURS
-- =========================

CREATE TABLE borrowers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================
-- PRETS
-- =========================

CREATE TABLE loans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    borrower_id BIGINT NOT NULL,
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status ENUM(
        'ACTIVE',
        'RETURNED',
        'LATE'
    )
    NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY(item_id)
        REFERENCES items(id)
        ON DELETE RESTRICT,
    FOREIGN KEY(borrower_id)
        REFERENCES borrowers(id)
        ON DELETE RESTRICT,
    INDEX idx_loans_active (
        item_id,
        status
    )
) ENGINE=InnoDB;