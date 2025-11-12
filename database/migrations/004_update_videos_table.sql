SET @schema_name := DATABASE();
SET @table_exists := (
    SELECT COUNT(*)
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = @schema_name
      AND TABLE_NAME = 'videos'
);

SET @ddl := IF(@table_exists = 0, 'SELECT 1', 'ALTER TABLE videos MODIFY COLUMN file_url VARCHAR(512) NOT NULL');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'videos' AND COLUMN_NAME = 'cover_url'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@column_exists = 0,
                  'ALTER TABLE videos ADD COLUMN cover_url VARCHAR(512) NULL AFTER description',
                  'ALTER TABLE videos MODIFY COLUMN cover_url VARCHAR(512) NULL'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'videos' AND COLUMN_NAME = 'tags'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@column_exists = 0,
                  'ALTER TABLE videos ADD COLUMN tags VARCHAR(255) NULL AFTER cover_url',
                  'ALTER TABLE videos MODIFY COLUMN tags VARCHAR(255) NULL'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'videos' AND COLUMN_NAME = 'duration_sec'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@column_exists = 0,
                  'ALTER TABLE videos ADD COLUMN duration_sec INT NULL AFTER tags',
                  'ALTER TABLE videos MODIFY COLUMN duration_sec INT NULL'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'videos' AND COLUMN_NAME = 'comment_count'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@column_exists = 0,
                  'ALTER TABLE videos ADD COLUMN comment_count BIGINT NOT NULL DEFAULT 0 AFTER duration_sec',
                  'ALTER TABLE videos MODIFY COLUMN comment_count BIGINT NOT NULL DEFAULT 0'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'videos' AND COLUMN_NAME = 'uploader_id'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@column_exists = 0,
                  'ALTER TABLE videos ADD COLUMN uploader_id BIGINT NULL AFTER comment_count',
                  'ALTER TABLE videos MODIFY COLUMN uploader_id BIGINT NULL'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'videos' AND COLUMN_NAME = 'uploader_name'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@column_exists = 0,
                  'ALTER TABLE videos ADD COLUMN uploader_name VARCHAR(50) NULL AFTER uploader_id',
                  'ALTER TABLE videos MODIFY COLUMN uploader_name VARCHAR(50) NULL'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'videos' AND COLUMN_NAME = 'like_count'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@column_exists = 0,
                  'ALTER TABLE videos ADD COLUMN like_count BIGINT NOT NULL DEFAULT 0 AFTER uploader_name',
                  'ALTER TABLE videos MODIFY COLUMN like_count BIGINT NOT NULL DEFAULT 0'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'videos' AND COLUMN_NAME = 'play_count'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@column_exists = 0,
                  'ALTER TABLE videos ADD COLUMN play_count BIGINT NOT NULL DEFAULT 0 AFTER like_count',
                  'ALTER TABLE videos MODIFY COLUMN play_count BIGINT NOT NULL DEFAULT 0'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               'ALTER TABLE videos MODIFY COLUMN status TINYINT NOT NULL DEFAULT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'videos' AND COLUMN_NAME = 'create_time'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@column_exists = 0,
                  'ALTER TABLE videos ADD COLUMN create_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP AFTER status',
                  'ALTER TABLE videos MODIFY COLUMN create_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'videos' AND COLUMN_NAME = 'update_time'
);
SET @ddl := IF(@table_exists = 0, 'SELECT 1',
               IF(@column_exists = 0,
                  'ALTER TABLE videos ADD COLUMN update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER create_time',
                  'ALTER TABLE videos MODIFY COLUMN update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP'));
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
