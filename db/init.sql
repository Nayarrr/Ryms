CREATE DATABASE ryms_database;

\c ryms_database;

-- 1. Create Tables
CREATE TABLE IF NOT EXISTS users (
    email VARCHAR(100) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar BYTEA,
    role VARCHAR(20) DEFAULT 'USER'
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

CREATE TABLE IF NOT EXISTS games (
    game_id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    editor VARCHAR(255),
    releaseDate TIMESTAMP,
    logo BYTEA
);

CREATE TABLE IF NOT EXISTS products (
    product_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    category VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS tournaments (
    tournament_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    game_id INT REFERENCES games(game_id) ON DELETE RESTRICT,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    max_participants INT,
    location VARCHAR(255),
    status VARCHAR(50) DEFAULT 'PLANNING'
);

CREATE TABLE IF NOT EXISTS matchs(
    match_id SERIAL PRIMARY KEY,
    tournament_id INT NOT NULL REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
    game_id INT REFERENCES games(game_id) ON DELETE RESTRICT,
    match_date TIMESTAMP NOT NULL,
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
    match_id INT NOT NULL,
    team_id INT NOT NULL,
    PRIMARY KEY (match_id, team_id),
    FOREIGN KEY (match_id) REFERENCES matchs(match_id) ON DELETE CASCADE,
    FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS match_results (
    match_id INT REFERENCES matchs(match_id) ON DELETE CASCADE,
    team_id INT REFERENCES teams(team_id) ON DELETE CASCADE,
    score INT NOT NULL DEFAULT 0,
    result VARCHAR(10) CHECK (result IN ('WIN', 'LOSS', 'DRAW')),
    PRIMARY KEY (match_id, team_id)
);

CREATE TABLE IF NOT EXISTS tournament_registrations (
    registration_id BIGSERIAL PRIMARY KEY,
    tournament_id INTEGER NOT NULL,
    team_id BIGINT NOT NULL,
    registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_tournament 
        FOREIGN KEY (tournament_id) 
        REFERENCES tournaments(tournament_id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_team 
        FOREIGN KEY (team_id) 
        REFERENCES teams(team_id) 
        ON DELETE CASCADE,
    CONSTRAINT unique_team_tournament 
        UNIQUE (tournament_id, team_id)
);

-- 2. Seed Data
-- Users
INSERT INTO users (email, username, password, role) VALUES
    ('admin@ryms.com', 'admin', 'password_123', 'ADMIN'),
    ('test@ryms.com', 'testuser', 'password_456', 'USER'),
    ('raja@ryms.com', 'randomuser', 'password_789', 'USER')
ON CONFLICT (email) DO NOTHING;

-- Games (avec game_id auto-généré)
INSERT INTO games (name, editor) VALUES
    ('Rocket League', 'Psyonix'),
    ('League of Legends', 'Riot Games'),
    ('VALORANT', 'Riot Games')
ON CONFLICT (name) DO NOTHING;

-- Teams (sans spécifier team_id pour utiliser SERIAL auto-increment)
INSERT INTO teams (name, tag, avatar, captain_email) VALUES
    ('GentleMates', 'M16','https://liquipedia.net/commons/images/thumb/c/c0/Gentle_Mates_2024_lightmode.png/600px-Gentle_Mates_2024_lightmode.png', 'admin@ryms.com'),
    ('Karmine Corp', 'KCB', 'https://liquipedia.net/commons/images/thumb/e/e1/Karmine_Corp_full_lightmode.png/600px-Karmine_Corp_full_lightmode.png', 'test@ryms.com'),
    ('Vitality', 'Vita', 'https://liquipedia.net/commons/images/thumb/e/e4/Team_Vitality_2023_lightmode.png/494px-Team_Vitality_2023_lightmode.png', 'raja@ryms.com')
ON CONFLICT (name) DO NOTHING;

-- Synchroniser la séquence après les insertions manuelles (si la table existait déjà)
SELECT setval('teams_team_id_seq', COALESCE((SELECT MAX(team_id) FROM teams), 0) + 1, false);