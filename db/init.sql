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

CREATE TABLE IF NOT EXISTS products (
    product_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    category VARCHAR(50)
);

-- WARNING: These seed users contain plain text passwords for development/testing only.
-- In production, passwords MUST be hashed using a strong algorithm (bcrypt, scrypt, or Argon2)
-- before storage. The application layer should implement password hashing.
INSERT INTO users (email, username, password) VALUES
('admin@ryms.com', 'admin', 'password_123'),
('test@ryms.com', 'testuser', 'password_456');

ALTER TABLE users ADD COLUMN role VARCHAR(20) DEFAULT 'USER';
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@ryms.com';
('test@ryms.com', 'testuser', 'password_456'),
('raja@ryms.com', 'randomuser', 'password_789');

INSERT INTO teams (team_id, name, tag, avatar, captain_email) VALUES
(1, 'GentleMates', 'M16','https://liquipedia.net/commons/images/thumb/c/c0/Gentle_Mates_2024_lightmode.png/600px-Gentle_Mates_2024_lightmode.png', 'admin@ryms.com'),
(2, 'Karmine Corp', 'KCB', 'https://liquipedia.net/commons/images/thumb/e/e1/Karmine_Corp_full_lightmode.png/600px-Karmine_Corp_full_lightmode.png', 'test@ryms.com'),
(3, 'Vitality', 'Vita', 'https://liquipedia.net/commons/images/thumb/e/e4/Team_Vitality_2023_lightmode.png/494px-Team_Vitality_2023_lightmode.png', 'ra@ryms.com');

CREATE TABLE IF NOT EXISTS matchs(
    match_id SERIAL PRIMARY KEY,
    match_date TIMESTAMP NOT NULL,
    game_id INT NOT NULL, 
    status VARCHAR(20) DEFAULT 'SCHEDULED'
);

CREATE TABLE IF NOT EXISTS match_referees(
    match_id INTEGER NOT NULL,
    referee_email VARCHAR(100) NOT NULL,
    PRIMARY KEY (match_id, referee_email),
    FOREIGN KEY (match_id) REFERENCES matchs(match_id) ON DELETE CASCADE,
    FOREIGN KEY (referee_email) REFERENCES users(email) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS match_teams (
    match_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    PRIMARY KEY (match_id, team_id),
    FOREIGN KEY (match_id) REFERENCES matchs(match_id) ON DELETE CASCADE,
    FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS match_results (
    match_id BIGINT REFERENCES matchs(match_id) ON DELETE CASCADE,
    team_id INT REFERENCES teams(team_id) ON DELETE CASCADE,
    score INT NOT NULL DEFAULT 0,
    result VARCHAR(10) CHECK (result IN ('WIN', 'LOSS', 'DRAW')),
    PRIMARY KEY (match_id, team_id)
);
