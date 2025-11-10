-- ============================================
-- Company Demo Database Schema
-- ============================================

CREATE DATABASE IF NOT EXISTS company CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE company;

-- 1. users table
CREATE TABLE IF NOT EXISTS users (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    role         VARCHAR(20)  NOT NULL DEFAULT 'USER',
    status       TINYINT      NOT NULL DEFAULT 1,
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='platform users';

-- 2. videos table
CREATE TABLE IF NOT EXISTS videos (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    title          VARCHAR(120) NOT NULL,
    description    VARCHAR(500),
    file_url       VARCHAR(500) NOT NULL,
    cover_url      VARCHAR(500),
    uploader_id    BIGINT       NOT NULL,
    uploader_name  VARCHAR(64),
    like_count     BIGINT       NOT NULL DEFAULT 0,
    play_count     BIGINT       NOT NULL DEFAULT 0,
    status         TINYINT      NOT NULL DEFAULT 1,
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_uploader (uploader_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='video metadata';

-- 3. user behavior logs
CREATE TABLE IF NOT EXISTS user_behavior_logs (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    video_id    BIGINT       NOT NULL,
    action      VARCHAR(32)  NOT NULL,
    detail      VARCHAR(255),
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_user (user_id),
    KEY idx_video (video_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='user behavior audit';

-- Seed demo users (password: 123456)
INSERT INTO users (username, password, role, status)
VALUES
    ('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJw2', 'USER', 1),
    ('lisi',     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJw2', 'CREATOR', 1),
    ('wangwu',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJw2', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE username = VALUES(username);

-- Seed demo videos
INSERT INTO videos (title, description, file_url, cover_url, uploader_id, uploader_name, like_count, play_count)
VALUES
    ('Spring Cloud Quick Start', 'Microservice introduction clip', 'https://example.com/video/1.mp4', 'https://example.com/cover/1.jpg', 2, 'lisi', 5, 40),
    ('Java Tips', 'Short tips for Java beginners', 'https://example.com/video/2.mp4', 'https://example.com/cover/2.jpg', 2, 'lisi', 8, 55)
ON DUPLICATE KEY UPDATE title = VALUES(title);

-- Seed behavior logs
INSERT INTO user_behavior_logs (user_id, video_id, action, detail)
VALUES
    (1, 1, 'PLAY', 'watched 30s'),
    (1, 2, 'LIKE', 'great video'),
    (2, 1, 'PLAY', 'creator review');
