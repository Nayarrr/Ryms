CREATE DATABASE ryms_database;

\c ryms_database;

CREATE TABLE IF NOT EXISTS users (
    email VARCHAR(100) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar BYTEA
);

INSERT INTO users (email, username, password) VALUES
('admin@ryms.com', 'admin', 'password_123'),
('test@ryms.com', 'testuser', 'password_456');