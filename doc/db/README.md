# 数据库脚本说明

## 目录结构

```
doc/db/
├── V1__init.sql                          # 初始化脚本
├── V2__add_skills_and_combat.sql         # 增量更新V2
├── V3__add_quests_and_achievements.sql   # 增量更新V3
└── V4__unify_schema_for_backend.sql      # 统一schema（兼容当前后端代码）
```

## 数据库信息

- **MySQL 地址**: localhost:3306
- **数据库名**: npc_agent
- **用户名**: root
- **密码**: 123456

## 执行顺序

请按照以下顺序执行脚本：

1. **V1__init.sql** - 初始化脚本
   - 创建游戏核心数据表
   - 插入初始数据

2. **V2__add_skills_and_combat.sql** - 增量更新V2
   - 添加技能系统和战斗记录
   - 为玩家表添加灵石字段
   - 插入初始技能数据

3. **V3__add_quests_and_achievements.sql** - 增量更新V3
   - 添加任务系统和成就系统
   - 为玩家表添加成就点数字段
   - 插入初始任务和成就数据

4. **V4__unify_schema_for_backend.sql** - 增量更新V4
   - 补齐 `npc_character`、`dialogue_option`、`player_state` 表
   - 对齐 `story_node` 结构（content/next/prerequisite/is_initial/enabled）
   - 为 `player_inventory` 增加玩家-物品唯一约束

## 执行方式

### 方式一：命令行执行

```bash
# 连接到MySQL
mysql -h localhost -P 3306 -u root -p

# 创建数据库
CREATE DATABASE IF NOT EXISTS npc_agent DEFAULT CHARACTER SET utf8mb4;

# 使用数据库
USE npc_agent;

# 执行脚本
source V1__init.sql;
source V2__add_skills_and_combat.sql;
source V3__add_quests_and_achievements.sql;
source V4__unify_schema_for_backend.sql;
```

### 方式二：使用数据库管理工具

1. 使用 Navicat、MySQL Workbench 等工具连接到数据库
2. 创建数据库 `npc_agent`
3. 按顺序执行SQL脚本（V1 -> V2 -> V3 -> V4）

## 数据表说明

### 核心表（V1）

| 表名 | 说明 |
|------|------|
| player | 玩家信息表 |
| npc | NPC信息表 |
| scene | 场景信息表 |
| item | 道具信息表 |
| monster | 怪物信息表 |
| story_node | 剧情节点表 |
| player_inventory | 玩家背包表 |
| player_story_progress | 玩家剧情进度表 |
| dialogue_history | 对话历史表 |

### 扩展表（V2）

| 表名 | 说明 |
|------|------|
| player_skill | 玩家技能表 |
| combat_record | 战斗记录表 |

### 扩展表（V3）

| 表名 | 说明 |
|------|------|
| quest | 任务表 |
| player_quest | 玩家任务进度表 |
| achievement | 成就表 |
| player_achievement | 玩家成就进度表 |

### 兼容表（V4）

| 表名 | 说明 |
|------|------|
| npc_character | NPC角色兼容表（供当前后端查询） |
| dialogue_option | 对话选项兼容表（供双模式对话） |
| player_state | 玩家状态兼容表 |

## 注意事项

1. 执行脚本前请确保MySQL服务正在运行
2. 脚本使用 `IF NOT EXISTS`，可以重复执行
3. 增量更新脚本会修改表结构，请谨慎执行
4. 建议在执行前备份数据库
5. 统一schema后，建议优先使用 `doc/db` 下版本脚本，不再单独执行 `backend/src/main/resources/sql/init_database.sql`

## Redis 信息

- **Redis 地址**: localhost:6379
- **密码**: 无
- **数据库**: 0

Redis 用于缓存和会话管理，无需执行脚本，应用启动时会自动连接。
