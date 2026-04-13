-- =====================================================
-- 增量更新脚本 - 版本 V4
-- 描述: 统一 schema，兼容当前后端代码依赖
-- 目标:
-- 1) 补齐 npc_character / dialogue_option / player_state 表
-- 2) 为 story_node 增加 content / prerequisite_node_id / next_node_id / is_initial / enabled
-- 3) 为 player_inventory 增加玩家-物品唯一约束，避免重复行
-- =====================================================

-- 1) 补齐 npc_character（供 NpcCharacterMapper 使用）
CREATE TABLE IF NOT EXISTS `npc_character` (
    `code` VARCHAR(64) PRIMARY KEY COMMENT 'NPC代码',
    `name` VARCHAR(100) NOT NULL COMMENT 'NPC名称',
    `personality` VARCHAR(255) COMMENT '性格特点',
    `speaking_style` VARCHAR(255) COMMENT '说话风格',
    `scene_code` VARCHAR(64) COMMENT '场景代码',
    `description` TEXT COMMENT '角色描述',
    `initial_node_id` VARCHAR(64) COMMENT '初始剧情节点',
    `enabled` BOOLEAN DEFAULT TRUE COMMENT '是否启用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='NPC角色兼容表';

-- 从 V1 的 npc 表同步基础数据（首次执行有效）
INSERT INTO `npc_character` (`code`, `name`, `personality`, `speaking_style`, `scene_code`, `description`, `initial_node_id`, `enabled`)
SELECT n.npc_code, n.name, n.personality, n.speaking_style, n.scene_code, n.description, NULL, TRUE
FROM `npc` n
WHERE NOT EXISTS (
    SELECT 1 FROM `npc_character` nc WHERE nc.code = n.npc_code
);

-- 根据现有剧情节点补齐 initial_node_id（只在空值时更新）
UPDATE `npc_character` nc
JOIN (
    SELECT npc_code, MIN(node_id) AS initial_node_id
    FROM story_node
    WHERE npc_code IS NOT NULL AND npc_code <> ''
    GROUP BY npc_code
) t ON t.npc_code = nc.code
SET nc.initial_node_id = t.initial_node_id
WHERE nc.initial_node_id IS NULL;

-- 2) 补齐 player_state（供 PlayerStateMapper 使用）
CREATE TABLE IF NOT EXISTS `player_state` (
    `player_id` VARCHAR(64) PRIMARY KEY COMMENT '玩家ID',
    `current_scene` VARCHAR(64) DEFAULT 'village' COMMENT '当前场景',
    `current_level` INT DEFAULT 1 COMMENT '当前等级',
    `cultivation_level` INT DEFAULT 0 COMMENT '修为等级',
    `experience` INT DEFAULT 0 COMMENT '经验值',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家状态兼容表';

INSERT INTO `player_state` (`player_id`, `current_scene`, `current_level`, `cultivation_level`, `experience`)
SELECT p.player_id, p.current_scene, p.level, p.cultivation_level, p.experience
FROM `player` p
WHERE NOT EXISTS (
    SELECT 1 FROM `player_state` ps WHERE ps.player_id = p.player_id
);

-- 3) 补齐 dialogue_option（供 DialogueOptionMapper 使用）
CREATE TABLE IF NOT EXISTS `dialogue_option` (
    `option_id` VARCHAR(64) PRIMARY KEY COMMENT '选项ID',
    `text` TEXT NOT NULL COMMENT '选项文本',
    `npc_code` VARCHAR(64) COMMENT 'NPC代码',
    `node_id` VARCHAR(64) COMMENT '剧情节点ID',
    `next_node_id` VARCHAR(64) COMMENT '后续节点ID',
    `response_template` TEXT COMMENT '回复模板',
    `order_index` INT DEFAULT 0 COMMENT '排序',
    `enabled` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    INDEX `idx_npc_code` (`npc_code`),
    INDEX `idx_node_id` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话选项兼容表';

-- 仅在无数据时注入基础选项，避免覆盖已有配置
INSERT INTO `dialogue_option` (`option_id`, `text`, `npc_code`, `node_id`, `next_node_id`, `response_template`, `order_index`, `enabled`)
SELECT 'village_001_opt_1', '我想拜师修仙', 'elder', 'village_001', 'village_002', '年轻人有志向，修仙之路艰险，但贵在恒心。', 1, TRUE
WHERE NOT EXISTS (SELECT 1 FROM dialogue_option LIMIT 1);

INSERT INTO `dialogue_option` (`option_id`, `text`, `npc_code`, `node_id`, `next_node_id`, `response_template`, `order_index`, `enabled`)
SELECT 'village_001_opt_2', '请问灵根测试在哪里？', 'elder', 'village_001', 'village_002', '镇东有一处聚灵台，你可前往一试。', 2, TRUE
WHERE NOT EXISTS (SELECT 1 FROM dialogue_option LIMIT 1);

INSERT INTO `dialogue_option` (`option_id`, `text`, `npc_code`, `node_id`, `next_node_id`, `response_template`, `order_index`, `enabled`)
SELECT 'village_002_opt_1', '我现在就去测试灵根', 'elder', 'village_002', 'village_003', '切记守心静气，莫要急躁。', 1, TRUE
WHERE NOT EXISTS (SELECT 1 FROM dialogue_option LIMIT 1);

-- 4) 对齐 story_node 字段，兼容 StoryNode 实体
ALTER TABLE `story_node` ADD COLUMN IF NOT EXISTS `content` TEXT COMMENT '剧情内容';
ALTER TABLE `story_node` ADD COLUMN IF NOT EXISTS `prerequisite_node_id` VARCHAR(64) COMMENT '前置节点ID';
ALTER TABLE `story_node` ADD COLUMN IF NOT EXISTS `next_node_id` VARCHAR(64) COMMENT '后续节点ID';
ALTER TABLE `story_node` ADD COLUMN IF NOT EXISTS `is_initial` BOOLEAN DEFAULT FALSE COMMENT '是否初始节点';
ALTER TABLE `story_node` ADD COLUMN IF NOT EXISTS `enabled` BOOLEAN DEFAULT TRUE COMMENT '是否启用';

-- 用已有字段回填 content，避免向量初始化时为空
UPDATE `story_node`
SET `content` = COALESCE(`content`, `npc_reply_template`, `description`)
WHERE `content` IS NULL;

-- 根据现有 JSON 形态的 next_nodes 回填 next_node_id（仅提取第一个）
UPDATE `story_node`
SET `next_node_id` = TRIM(BOTH '"' FROM JSON_UNQUOTE(JSON_EXTRACT(`next_nodes`, '$[0]')))
WHERE (`next_node_id` IS NULL OR `next_node_id` = '')
  AND `next_nodes` IS NOT NULL
  AND JSON_VALID(`next_nodes`);

-- 根据前置关系推导初始节点（无前置即初始）
UPDATE `story_node`
SET `is_initial` = TRUE
WHERE (`prerequisite_nodes` IS NULL OR `prerequisite_nodes` = '[]' OR `prerequisite_nodes` = '');

-- 5) 约束 player_inventory 唯一行，便于库存 upsert
ALTER TABLE `player_inventory`
ADD UNIQUE KEY `uk_player_item` (`player_id`, `item_id`);
