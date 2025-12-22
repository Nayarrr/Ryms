CREATE DATABASE ryms_database;

\c ryms_database;

CREATE TABLE IF NOT EXISTS users (
    email VARCHAR(100) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar BYTEA
);

CREATE TABLE IF NOT EXISTS teams (
    team_id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    tag VARCHAR(10) NOT NULL,
    avatar VARCHAR(255),
    captain_email VARCHAR(255) REFERENCES users(email),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS team_members (
    team_id INT REFERENCES teams(team_id) ON DELETE CASCADE,
    user_email VARCHAR(255) REFERENCES users(email) ON DELETE CASCADE,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (team_id, user_email)
);

CREATE TABLE IF NOT EXISTS invitations (
    invitation_id SERIAL PRIMARY KEY,
    team_id INT REFERENCES teams(team_id) ON DELETE CASCADE,
    sender_email VARCHAR(255) REFERENCES users(email),
    receiver_email VARCHAR(255) REFERENCES users(email),
    status VARCHAR(50) DEFAULT 'PENDING',
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (email, username, password) VALUES
('admin@ryms.com', 'admin', 'password_123'),
('test@ryms.com', 'testuser', 'password_456');