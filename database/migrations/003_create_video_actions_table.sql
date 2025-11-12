SET @schema_name := DATABASE();
SET @table_exists := (
    SELECT COUNT(*)
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = @schema_name
      AND TABLE_NAME = 'video_actions'
);

SET @ddl := IF(@table_exists = 0,
               'CREATE TABLE video_actions (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    user_id BIGINT NOT NULL,
                    video_id BIGINT NOT NULL,
                    action ENUM(''view'',''like'',''comment'') NOT NULL,
                    weight INT NOT NULL DEFAULT 0,
                    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    KEY idx_user (user_id),
                    KEY idx_video (video_id),
                    KEY idx_video_action (video_id, action)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''video action weights''',
               'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'video_actions' AND COLUMN_NAME = 'action'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@column_exists = 0,
                  'ALTER TABLE video_actions ADD COLUMN action ENUM(''view'',''like'',''comment'') NOT NULL DEFAULT ''view'' AFTER video_id',
                  'ALTER TABLE video_actions MODIFY COLUMN action ENUM(''view'',''like'',''comment'') NOT NULL DEFAULT ''view'''));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'video_actions' AND COLUMN_NAME = 'weight'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@column_exists = 0,
                  'ALTER TABLE video_actions ADD COLUMN weight INT NOT NULL DEFAULT 0 AFTER action',
                  'ALTER TABLE video_actions MODIFY COLUMN weight INT NOT NULL DEFAULT 0'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_create_time := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'video_actions' AND COLUMN_NAME = 'create_time'
);
SET @has_action_time := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'video_actions' AND COLUMN_NAME = 'action_time'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@has_create_time = 0 AND @has_action_time = 1,
                  'ALTER TABLE video_actions CHANGE COLUMN action_time create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP',
                  IF(@has_create_time = 0,
                     'ALTER TABLE video_actions ADD COLUMN create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP',
                     'ALTER TABLE video_actions MODIFY COLUMN create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP')));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'video_actions' AND INDEX_NAME = 'idx_user'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@index_exists = 0,
                  'ALTER TABLE video_actions ADD INDEX idx_user (user_id)',
                  'SELECT 1'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'video_actions' AND INDEX_NAME = 'idx_video'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@index_exists = 0,
                  'ALTER TABLE video_actions ADD INDEX idx_video (video_id)',
                  'SELECT 1'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists := (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'video_actions' AND INDEX_NAME = 'idx_video_action'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@index_exists = 0,
                  'ALTER TABLE video_actions ADD INDEX idx_video_action (video_id, action)',
                  'SELECT 1'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
