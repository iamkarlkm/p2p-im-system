# 地理围栏服务数据库初始化

## 快速开始

### 1. 创建数据库

在 MySQL 中执行以下命令：

```bash
mysql -u root -p
```

然后执行 SQL 脚本：

```sql
source multi_agent/projects/im-modular/im-service/im-service-geofence/init-scripts/init-database.sql
```

或者直接在 MySQL 客户端中复制粘贴 `init-database.sql` 的内容执行。

### 2. 数据库结构

创建的数据库包含以下表：

| 表名 | 说明 | 核心字段 |
|------|------|---------|
| `im_geofence` | 地理围栏表 | geofence_id, name, fence_type, coordinates, geo_hash |
| `im_location_share` | 位置分享表 | share_id, user_id, latitude, longitude, expires_at |
| `im_geofence_event` | 围栏事件表 | event_id, geofence_id, user_id, event_type, event_time |

### 3. 表关系

```
im_geofence (1) ----< (N) im_geofence_event
    |                      |
    |                      |
    v                      v
im_location_share      (记录)
(独立)
```

### 4. 索引说明

**im_geofence 索引**:
- `idx_merchant_id` - 商户查询
- `idx_geo_hash` - 附近围栏查询
- `idx_status` + `idx_enabled` - 激活围栏过滤

**im_location_share 索引**:
- `idx_user_id` + `idx_is_active` - 用户活跃分享查询
- `idx_expires_at` - 过期清理

**im_geofence_event 索引**:
- `idx_geofence_id` + `idx_user_id` - 围栏用户事件查询
- `idx_user_event_time` - 用户事件历史查询

### 5. 常用查询示例

```sql
-- 查询商户的所有围栏
SELECT * FROM im_geofence WHERE merchant_id = 'xxx';

-- 查询激活的围栏
SELECT * FROM im_geofence WHERE status = 'ACTIVE' AND is_enabled = 1;

-- 根据GeoHash前缀查询附近围栏
SELECT * FROM im_geofence WHERE geo_hash LIKE 'wx4g%';

-- 查询用户的活跃位置分享
SELECT * FROM im_location_share WHERE user_id = 'xxx' AND is_active = 1;

-- 查询围栏的事件记录
SELECT * FROM im_geofence_event WHERE geofence_id = 'xxx' ORDER BY event_time DESC LIMIT 50;
```

### 6. 配置检查

确保 `application.yml` 中的数据库配置正确：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/im_geofence?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 7. 启动服务

```bash
cd im-service-geofence
mvn spring-boot:run
```

服务将自动创建/更新表结构（`spring.jpa.hibernate.ddl-auto: update`）
