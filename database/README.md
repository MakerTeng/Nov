# Database Schema

This project uses a single MySQL schema named `company`. Run `schema.sql` to create all tables and insert demo data.

```sql
source database/schema.sql;
```

## Tables

### users
| Column | Type | Notes |
| --- | --- | --- |
| id | BIGINT | primary key |
| username | VARCHAR(50) | unique login name |
| password | VARCHAR(255) | BCrypt hash |
| role | VARCHAR(20) | USER / CREATOR / ADMIN |
| status | TINYINT | 1 enabled, 0 locked |
| create_time / update_time | DATETIME | audit columns |

### videos
Stores video metadata (actual media files live on object storage / CDN).

| Column | Type | Notes |
| --- | --- | --- |
| id | BIGINT | primary key |
| title / description | VARCHAR | basic info |
| file_url / cover_url | VARCHAR | media urls |
| uploader_id / uploader_name | | owner info |
| like_count / play_count | BIGINT | statistics |
| status | TINYINT | 1=online, 0=offline |

### user_behavior_logs
Every user action that should drive recommendations is persisted here and also published to MQ.

| Column | Type | Notes |
| --- | --- | --- |
| id | BIGINT | primary key |
| user_id | BIGINT | actor |
| video_id | BIGINT | target video |
| action | VARCHAR(32) | PLAY / LIKE / COMMENT ... |
| detail | VARCHAR(255) | optional description |

## Seed data

`schema.sql` inserts:
1. Three demo accounts: user, creator, admin (password `123456`).
2. Two sample videos uploaded by the creator.
3. A couple of behavior log rows to feed the recommendation service.

Feel free to replace the seed data with your own while keeping the table definitions unchanged.
