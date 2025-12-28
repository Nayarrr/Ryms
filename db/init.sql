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

-- WARNING: These seed users contain plain text passwords for development/testing only.
-- In production, passwords MUST be hashed using a strong algorithm (bcrypt, scrypt, or Argon2)
-- before storage. The application layer should implement password hashing.
INSERT INTO users (email, username, password) VALUES
('admin@ryms.com', 'admin', 'password_123'),
('test@ryms.com', 'testuser', 'password_456');

CREATE TABLE IF NOT EXISTS matchs(
    match_id SERIAL PRIMARY KEY,
    match_date TIMESTAMP NOT NULL,
    game_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS match_referees(
    match_id INTEGER NOT NULL,
    referee_email VARCHAR(100) NOT NULL,
    PRIMARY KEY (match_id, referee_email),
    FOREIGN KEY (match_id) REFERENCES matchs(match_id) ON DELETE CASCADE,
    FOREIGN KEY (referee_email) REFERENCES users(email) ON DELETE CASCADE
);

INSERT INTO matchs (match_date, game_id) VALUES
('2025-01-15 14:00:00', 1),
('2025-01-20 16:30:00', 2),
('2025-02-01 18:00:00', 1);


CREATE TABLE IF NOT EXISTS teams (
    team_id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    tag VARCHAR(10) NOT NULL,
    avatar VARCHAR(255),
    captain_email VARCHAR(255) REFERENCES users(email),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO teams (name, tag, captain_email) VALUES
('Team Alpha', 'ALPH', 'admin@ryms.com'),
('Team Beta', 'BETA', 'test@ryms.com'),
('Team Gamma', 'GAMM', 'admin@ryms.com'),
('Team Delta', 'DELT', 'test@ryms.com'),
('Team Epsilon', 'EPSI', 'admin@ryms.com'),
('Team Zeta', 'ZETA', 'test@ryms.com');

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