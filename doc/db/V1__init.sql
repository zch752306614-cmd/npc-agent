-- =====================================================
-- 初始化脚本 - 创建所有基础表
-- 版本: V1
-- 描述: 创建游戏核心数据表
-- =====================================================

-- 创建玩家表
CREATE TABLE IF NOT EXISTS `player` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `player_id` VARCHAR(64) NOT NULL UNIQUE COMMENT '玩家唯一标识',
    `name` VARCHAR(100) NOT NULL COMMENT '玩家名称',
    `level` INT DEFAULT 1 COMMENT '等级',
    `cultivation_level` INT DEFAULT 0 COMMENT '修为等级',
    `realm` VARCHAR(50) DEFAULT '凡人' COMMENT '境界（炼气、筑基、金丹等）',
    `spiritual_power` INT DEFAULT 100 COMMENT '灵力',
    `max_spiritual_power` INT DEFAULT 100 COMMENT '最大灵力',
    `health` INT DEFAULT 100 COMMENT '生命值',
    `max_health` INT DEFAULT 100 COMMENT '最大生命值',
    `attack` INT DEFAULT 10 COMMENT '攻击力',
    `defense` INT DEFAULT 5 COMMENT '防御力',
    `experience` INT DEFAULT 0 COMMENT '经验值',
    `current_scene` VARCHAR(64) DEFAULT 'village' COMMENT '当前场景',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_player_id` (`player_id`),
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家表';

-- 创建NPC表
CREATE TABLE IF NOT EXISTS `npc` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `npc_code` VARCHAR(64) NOT NULL UNIQUE COMMENT 'NPC代码',
    `name` VARCHAR(100) NOT NULL COMMENT 'NPC名称',
    `description` TEXT COMMENT 'NPC描述',
    `personality` VARCHAR(200) COMMENT '性格特点',
    `speaking_style` VARCHAR(200) COMMENT '说话风格',
    `scene_code` VARCHAR(64) COMMENT '所在场景',
    `dialogue_library` TEXT COMMENT '对话库（JSON格式）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_npc_code` (`npc_code`),
    INDEX `idx_scene_code` (`scene_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='NPC表';

-- 创建场景表
CREATE TABLE IF NOT EXISTS `scene` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `scene_code` VARCHAR(64) NOT NULL UNIQUE COMMENT '场景代码',
    `name` VARCHAR(100) NOT NULL COMMENT '场景名称',
    `description` TEXT COMMENT '场景描述',
    `type` VARCHAR(50) DEFAULT 'normal' COMMENT '场景类型（normal、danger、secret）',
    `level_requirement` INT DEFAULT 0 COMMENT '进入等级要求',
    `npcs` TEXT COMMENT 'NPC列表（JSON格式）',
    `monsters` TEXT COMMENT '怪物列表（JSON格式）',
    `resource_points` TEXT COMMENT '资源点（JSON格式）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_scene_code` (`scene_code`),
    INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景表';

-- 创建道具表
CREATE TABLE IF NOT EXISTS `item` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '道具名称',
    `type` VARCHAR(50) NOT NULL COMMENT '道具类型（weapon、armor、accessory、pill、material、consumable）',
    `rarity` INT DEFAULT 1 COMMENT '稀有度（1-普通，2-优秀，3-稀有，4-史诗，5-传说）',
    `description` TEXT COMMENT '道具描述',
    `attack` INT DEFAULT 0 COMMENT '攻击力（武器）',
    `defense` INT DEFAULT 0 COMMENT '防御力（防具）',
    `health_effect` INT DEFAULT 0 COMMENT '生命效果（丹药）',
    `spiritual_power_effect` INT DEFAULT 0 COMMENT '灵力效果（丹药）',
    `stackable` BOOLEAN DEFAULT TRUE COMMENT '是否可堆叠',
    `max_stack` INT DEFAULT 99 COMMENT '最大堆叠数量',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_name` (`name`),
    INDEX `idx_type` (`type`),
    INDEX `idx_rarity` (`rarity`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='道具表';

-- 创建怪物表
CREATE TABLE IF NOT EXISTS `monster` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '怪物名称',
    `level` INT DEFAULT 1 COMMENT '等级',
    `health` INT DEFAULT 100 COMMENT '生命值',
    `max_health` INT DEFAULT 100 COMMENT '最大生命值',
    `attack` INT DEFAULT 10 COMMENT '攻击力',
    `defense` INT DEFAULT 5 COMMENT '防御力',
    `spiritual_power` INT DEFAULT 20 COMMENT '灵力',
    `experience_reward` INT DEFAULT 10 COMMENT '经验奖励',
    `scene_code` VARCHAR(64) COMMENT '所在场景',
    `drop_items` TEXT COMMENT '掉落物品（JSON格式）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_name` (`name`),
    INDEX `idx_level` (`level`),
    INDEX `idx_scene_code` (`scene_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='怪物表';

-- 创建剧情节点表
CREATE TABLE IF NOT EXISTS `story_node` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `node_id` VARCHAR(64) NOT NULL UNIQUE COMMENT '节点ID',
    `npc_code` VARCHAR(64) COMMENT '关联NPC代码',
    `scene_code` VARCHAR(64) COMMENT '场景代码',
    `title` VARCHAR(200) COMMENT '节点标题',
    `description` TEXT COMMENT '节点描述',
    `npc_reply_template` TEXT COMMENT 'NPC回复模板',
    `keywords` TEXT COMMENT '关键词（JSON格式）',
    `rewards` TEXT COMMENT '奖励（JSON格式）',
    `next_nodes` TEXT COMMENT '后续节点（JSON格式）',
    `prerequisite_nodes` TEXT COMMENT '前置节点（JSON格式）',
    `is_branch` BOOLEAN DEFAULT FALSE COMMENT '是否为分支节点',
    `branch_options` TEXT COMMENT '分支选项（JSON格式）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_node_id` (`node_id`),
    INDEX `idx_npc_code` (`npc_code`),
    INDEX `idx_scene_code` (`scene_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='剧情节点表';

-- 创建玩家背包表
CREATE TABLE IF NOT EXISTS `player_inventory` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `player_id` VARCHAR(64) NOT NULL COMMENT '玩家ID',
    `item_id` BIGINT NOT NULL COMMENT '道具ID',
    `quantity` INT DEFAULT 1 COMMENT '数量',
    `equipped` BOOLEAN DEFAULT FALSE COMMENT '是否已装备',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_player_id` (`player_id`),
    INDEX `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家背包表';

-- 创建玩家剧情进度表
CREATE TABLE IF NOT EXISTS `player_story_progress` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `player_id` VARCHAR(64) NOT NULL COMMENT '玩家ID',
    `node_id` VARCHAR(64) NOT NULL COMMENT '完成的节点ID',
    `completed_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '完成时间',
    INDEX `idx_player_id` (`player_id`),
    INDEX `idx_node_id` (`node_id`),
    UNIQUE KEY `uk_player_node` (`player_id`, `node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家剧情进度表';

-- 创建对话历史表
CREATE TABLE IF NOT EXISTS `dialogue_history` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `player_id` VARCHAR(64) NOT NULL COMMENT '玩家ID',
    `npc_code` VARCHAR(64) NOT NULL COMMENT 'NPC代码',
    `speaker` VARCHAR(50) NOT NULL COMMENT '说话者（player/npc）',
    `text` TEXT NOT NULL COMMENT '对话内容',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_player_npc` (`player_id`, `npc_code`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话历史表';

-- 插入初始数据
INSERT INTO `scene` (`scene_code`, `name`, `description`, `type`, `level_requirement`) VALUES
('village', '青牛镇', '一个宁静的山村，位于青云山脚下，是通往青云门的必经之路', 'normal', 0),
('mountain', '青云山', '云雾缭绕的修仙圣地，青云门所在地，山巅有千年古刹', 'normal', 10),
('cave', '神秘洞穴', '青云山后山的一处隐秘洞穴，据说藏有上古传承', 'secret', 20);

INSERT INTO `npc` (`npc_code`, `name`, `description`, `personality`, `speaking_style`, `scene_code`) VALUES
('elder', '老者', '一位慈祥的老者，在村里居住多年，对修仙之事略知一二', '慈祥、温和', '说话慢条斯理，喜欢用比喻', 'village'),
('sectMaster', '掌门', '青云门掌门，修为高深，威严而睿智', '威严、睿智', '说话简洁有力，富有哲理', 'mountain'),
('mysteriousElder', '神秘老者', '洞穴中的神秘人物，似乎在等待有缘人', '神秘、高深', '说话晦涩难懂，充满禅意', 'cave');

INSERT INTO `item` (`name`, `type`, `rarity`, `description`, `health_effect`, `spiritual_power_effect`, `stackable`) VALUES
('灵草', 'material', 1, '用于炼丹的基础材料', 0, 0, TRUE),
('灵石', 'consumable', 1, '修仙界通用货币', 0, 0, TRUE),
('基础丹药', 'pill', 2, '恢复少量灵力', 0, 20, TRUE),
('中级丹药', 'pill', 3, '恢复中等灵力', 0, 50, TRUE),
('高级丹药', 'pill', 4, '恢复大量灵力', 0, 100, TRUE);

INSERT INTO `monster` (`name`, `level`, `health`, `max_health`, `attack`, `defense`, `spiritual_power`, `experience_reward`, `scene_code`) VALUES
('青狼', 1, 80, 80, 15, 5, 20, 10, 'village'),
('妖虎', 5, 200, 200, 30, 10, 50, 50, 'mountain'),
('守护兽', 10, 500, 500, 50, 20, 100, 100, 'cave');

INSERT INTO `story_node` (`node_id`, `npc_code`, `scene_code`, `title`, `description`, `npc_reply_template`, `keywords`, `rewards`, `next_nodes`, `prerequisite_nodes`, `is_branch`) VALUES
('village_001', 'elder', 'village', '老者引导', '初次见到老者，他似乎在等待什么人', '（慈祥地看着你）年轻人有志向，修仙之路虽艰险，但若心诚，必有所成。老朽当年也是从青云门外门弟子做起，虽资质平平，但也略知一二。', '["修仙", "拜师", "学习"]', '["修仙入门知识"]', '["village_002"]', '[]', FALSE),
('village_002', 'elder', 'village', '灵根测试', '老者指引你进行灵根测试', '（指向镇东方向）镇东有一座废弃的聚灵台，虽已荒废，但仍能感应灵根。你若有心，可去一试。记住，修仙之路，九死一生，需有决心。', '["灵根", "测试", "聚灵台"]', '["灵根测试结果"]', '["village_003", "village_004", "village_005"]', '["village_001"]', TRUE);
