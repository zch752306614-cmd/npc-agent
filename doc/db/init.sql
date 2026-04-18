-- =====================================================
-- NPC-Agent 数据库初始化脚本
-- 版本: V6
-- 描述: 综合所有历史版本，创建一个完整的数据库初始化脚本
-- 游戏时长: 约60分钟
-- 章节: 5章15个剧情节点
-- =====================================================

-- =====================================================
-- 第一部分：基础表结构
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
    `spirit_stones` INT DEFAULT 0 COMMENT '灵石数量',
    `achievement_points` INT DEFAULT 0 COMMENT '成就点数',
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

-- 创建NPC角色兼容表（供后端代码使用）
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
    `skill_name` VARCHAR(100) COMMENT '关联技能名称（如果是技能书）',
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
    `content` TEXT COMMENT '剧情内容',
    `npc_reply_template` TEXT COMMENT 'NPC回复模板',
    `keywords` TEXT COMMENT '关键词（JSON格式）',
    `rewards` TEXT COMMENT '奖励（JSON格式）',
    `next_nodes` TEXT COMMENT '后续节点（JSON格式）',
    `prerequisite_nodes` TEXT COMMENT '前置节点（JSON格式）',
    `prerequisite_node_id` VARCHAR(64) COMMENT '前置节点ID',
    `next_node_id` VARCHAR(64) COMMENT '后续节点ID',
    `is_branch` BOOLEAN DEFAULT FALSE COMMENT '是否为分支节点',
    `is_initial` BOOLEAN DEFAULT FALSE COMMENT '是否初始节点',
    `enabled` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `branch_options` TEXT COMMENT '分支选项（JSON格式）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_node_id` (`node_id`),
    INDEX `idx_npc_code` (`npc_code`),
    INDEX `idx_scene_code` (`scene_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='剧情节点表';

-- 创建对话选项表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话选项表';

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
    INDEX `idx_item_id` (`item_id`),
    UNIQUE KEY `uk_player_item` (`player_id`, `item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家背包表';

-- 创建玩家剧情进度表
CREATE TABLE IF NOT EXISTS `player_story_progress` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `player_id` VARCHAR(64) NOT NULL COMMENT '玩家ID',
    `node_id` VARCHAR(64) NOT NULL COMMENT '完成的节点ID',
    `completed` BOOLEAN DEFAULT FALSE COMMENT '是否已完成',
    `unlocked` BOOLEAN DEFAULT FALSE COMMENT '是否已解锁',
    `completed_at` TIMESTAMP NULL COMMENT '完成时间',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_player_id` (`player_id`),
    INDEX `idx_node_id` (`node_id`),
    UNIQUE KEY `uk_player_node` (`player_id`, `node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家剧情进度表';

-- 创建玩家状态表
CREATE TABLE IF NOT EXISTS `player_state` (
    `player_id` VARCHAR(64) PRIMARY KEY COMMENT '玩家ID',
    `current_scene` VARCHAR(64) DEFAULT 'village' COMMENT '当前场景',
    `current_level` INT DEFAULT 1 COMMENT '当前等级',
    `cultivation_level` INT DEFAULT 0 COMMENT '修为等级',
    `experience` INT DEFAULT 0 COMMENT '经验值',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家状态表';

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

-- =====================================================
-- 第二部分：技能和战斗系统
-- =====================================================

-- 创建玩家技能表
CREATE TABLE IF NOT EXISTS `player_skill` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `player_id` VARCHAR(64) NOT NULL COMMENT '玩家ID',
    `skill_name` VARCHAR(100) NOT NULL COMMENT '技能名称',
    `skill_level` INT DEFAULT 1 COMMENT '技能等级',
    `skill_type` VARCHAR(50) DEFAULT 'active' COMMENT '技能类型（active、passive）',
    `damage` INT DEFAULT 0 COMMENT '技能伤害',
    `cost` INT DEFAULT 0 COMMENT '灵力消耗',
    `cooldown` INT DEFAULT 0 COMMENT '冷却时间（秒）',
    `description` TEXT COMMENT '技能描述',
    `learned_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '学习时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_player_id` (`player_id`),
    INDEX `idx_skill_name` (`skill_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家技能表';

-- 创建战斗记录表
CREATE TABLE IF NOT EXISTS `combat_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `player_id` VARCHAR(64) NOT NULL COMMENT '玩家ID',
    `battle_id` VARCHAR(64) NOT NULL COMMENT '战斗ID',
    `monster_name` VARCHAR(100) COMMENT '怪物名称',
    `result` VARCHAR(20) NOT NULL COMMENT '战斗结果（win、lose、escape）',
    `player_health_before` INT COMMENT '玩家战斗前生命值',
    `player_health_after` INT COMMENT '玩家战斗后生命值',
    `monster_health_before` INT COMMENT '怪物战斗前生命值',
    `monster_health_after` INT COMMENT '怪物战斗后生命值',
    `rewards` TEXT COMMENT '奖励（JSON格式）',
    `experience_gained` INT DEFAULT 0 COMMENT '获得经验',
    `combat_time` INT COMMENT '战斗时长（秒）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '战斗时间',
    INDEX `idx_player_id` (`player_id`),
    INDEX `idx_battle_id` (`battle_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='战斗记录表';

-- =====================================================
-- 第三部分：任务和成就系统
-- =====================================================

-- 创建任务表
CREATE TABLE IF NOT EXISTS `quest` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `quest_code` VARCHAR(64) NOT NULL UNIQUE COMMENT '任务代码',
    `title` VARCHAR(200) NOT NULL COMMENT '任务标题',
    `description` TEXT COMMENT '任务描述',
    `type` VARCHAR(50) DEFAULT 'main' COMMENT '任务类型（main、side、daily）',
    `level_requirement` INT DEFAULT 0 COMMENT '等级要求',
    `prerequisite_quests` TEXT COMMENT '前置任务（JSON格式）',
    `objectives` TEXT COMMENT '任务目标（JSON格式）',
    `rewards` TEXT COMMENT '任务奖励（JSON格式）',
    `experience_reward` INT DEFAULT 0 COMMENT '经验奖励',
    `spirit_stones_reward` INT DEFAULT 0 COMMENT '灵石奖励',
    `is_repeatable` BOOLEAN DEFAULT FALSE COMMENT '是否可重复',
    `repeat_cooldown` INT DEFAULT 0 COMMENT '重复冷却时间（小时）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_quest_code` (`quest_code`),
    INDEX `idx_type` (`type`),
    INDEX `idx_level_requirement` (`level_requirement`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- 创建玩家任务进度表
CREATE TABLE IF NOT EXISTS `player_quest` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `player_id` VARCHAR(64) NOT NULL COMMENT '玩家ID',
    `quest_code` VARCHAR(64) NOT NULL COMMENT '任务代码',
    `status` VARCHAR(20) DEFAULT 'available' COMMENT '任务状态（available、in_progress、completed、failed）',
    `progress` TEXT COMMENT '任务进度（JSON格式）',
    `started_at` TIMESTAMP NULL COMMENT '开始时间',
    `completed_at` TIMESTAMP NULL COMMENT '完成时间',
    `last_repeat_at` TIMESTAMP NULL COMMENT '上次完成时间',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_player_id` (`player_id`),
    INDEX `idx_quest_code` (`quest_code`),
    INDEX `idx_status` (`status`),
    UNIQUE KEY `uk_player_quest` (`player_id`, `quest_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家任务进度表';

-- 创建成就表
CREATE TABLE IF NOT EXISTS `achievement` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `achievement_code` VARCHAR(64) NOT NULL UNIQUE COMMENT '成就代码',
    `title` VARCHAR(200) NOT NULL COMMENT '成就标题',
    `description` TEXT COMMENT '成就描述',
    `category` VARCHAR(50) DEFAULT 'general' COMMENT '成就分类（combat、exploration、story、social）',
    `condition_type` VARCHAR(50) NOT NULL COMMENT '条件类型（kill_monster、complete_quest、reach_level、explore_scene）',
    `condition_value` TEXT COMMENT '条件值（JSON格式）',
    `rewards` TEXT COMMENT '成就奖励（JSON格式）',
    `experience_reward` INT DEFAULT 0 COMMENT '经验奖励',
    `spirit_stones_reward` INT DEFAULT 0 COMMENT '灵石奖励',
    `hidden` BOOLEAN DEFAULT FALSE COMMENT '是否隐藏成就',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_achievement_code` (`achievement_code`),
    INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成就表';

-- 创建玩家成就进度表
CREATE TABLE IF NOT EXISTS `player_achievement` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `player_id` VARCHAR(64) NOT NULL COMMENT '玩家ID',
    `achievement_code` VARCHAR(64) NOT NULL COMMENT '成就代码',
    `progress` TEXT COMMENT '成就进度（JSON格式）',
    `completed` BOOLEAN DEFAULT FALSE COMMENT '是否完成',
    `completed_at` TIMESTAMP NULL COMMENT '完成时间',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_player_id` (`player_id`),
    INDEX `idx_achievement_code` (`achievement_code`),
    INDEX `idx_completed` (`completed`),
    UNIQUE KEY `uk_player_achievement` (`player_id`, `achievement_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家成就进度表';

-- =====================================================
-- 第四部分：初始场景数据
-- =====================================================

INSERT INTO `scene` (`scene_code`, `name`, `description`, `type`, `level_requirement`) VALUES
('village', '青牛镇', '一个宁静的山村，位于青云山脚下，是通往青云门的必经之路', 'normal', 0),
('mountain', '青云山', '云雾缭绕的修仙圣地，青云门所在地，山巅有千年古刹', 'normal', 10),
('cave', '神秘洞穴', '青云山后山的一处隐秘洞穴，据说藏有上古传承', 'secret', 20),
('ruins', '上古遗迹', '隐藏在上古时代的遗迹，充满机关和危险', 'danger', 30);

-- =====================================================
-- 第五部分：初始NPC数据
-- =====================================================

INSERT INTO `npc` (`npc_code`, `name`, `description`, `personality`, `speaking_style`, `scene_code`) VALUES
('elder', '村长', '青牛镇的村长，曾经是一位修仙者，退隐后回到家乡', '慈祥、智慧、乐于助人', '温和稳重，喜欢用比喻教导年轻人', 'village'),
('sectMaster', '掌门', '青云门掌门，修为高深，德高望重', '威严、公正、深谋远虑', '语气严肃，条理清晰，注重规矩', 'mountain'),
('seniorBrother', '师兄', '青云门的资深弟子，对新人很照顾', '热情、直率、经验丰富', '说话直接，喜欢开玩笑，乐于分享', 'mountain'),
('mysteriousElder', '神秘老者', '隐居在神秘洞穴的高人，对主角有特殊缘分', '神秘、深不可测、古道热肠', '语气高深，喜欢说禅语，点到为止', 'cave'),
('demonLeader', '魔修首领', '魔修势力的首领，试图抢夺上古神器', '邪恶、残忍、野心勃勃', '语气嚣张，充满恶意，喜欢嘲讽', 'ruins');

INSERT INTO `npc_character` (`code`, `name`, `personality`, `speaking_style`, `scene_code`, `description`, `initial_node_id`, `enabled`) VALUES
('elder', '村长', '慈祥、智慧、乐于助人', '温和稳重，喜欢用比喻教导年轻人', 'village', '青牛镇的村长，曾经是一位修仙者，退隐后回到家乡', 'node_1_1', TRUE),
('sectMaster', '掌门', '威严、公正、深谋远虑', '语气严肃，条理清晰，注重规矩', 'mountain', '青云门掌门，修为高深，德高望重', 'node_2_2', TRUE),
('seniorBrother', '师兄', '热情、直率、经验丰富', '说话直接，喜欢开玩笑，乐于分享', 'mountain', '青云门的资深弟子，对新人很照顾', 'node_3_1', TRUE),
('mysteriousElder', '神秘老者', '神秘、深不可测、古道热肠', '语气高深，喜欢说禅语，点到为止', 'cave', '隐居在神秘洞穴的高人，对主角有特殊缘分', 'node_4_3', TRUE),
('demonLeader', '魔修首领', '邪恶、残忍、野心勃勃', '语气嚣张，充满恶意，喜欢嘲讽', 'ruins', '魔修势力的首领，试图抢夺上古神器', 'node_5_2', TRUE);

-- =====================================================
-- 第六部分：初始道具数据
-- =====================================================

INSERT INTO `item` (`name`, `type`, `rarity`, `description`, `health_effect`, `spiritual_power_effect`, `stackable`, `skill_name`) VALUES
('灵草', 'material', 1, '用于炼丹的基础材料', 0, 0, TRUE, NULL),
('灵石', 'consumable', 1, '修仙界通用货币', 0, 0, TRUE, NULL),
('基础丹药', 'pill', 2, '恢复少量灵力', 0, 20, TRUE, NULL),
('中级丹药', 'pill', 3, '恢复中等灵力', 0, 50, TRUE, NULL),
('高级丹药', 'pill', 4, '恢复大量灵力', 0, 100, TRUE, NULL),
('基础吐纳诀', 'consumable', 2, '修仙基础功法，学习后可缓慢恢复灵力', 0, 0, FALSE, '基础吐纳'),
('火球术秘籍', 'consumable', 3, '基础攻击法术秘籍，学习后可使用火球术', 0, 0, FALSE, '火球术');

-- =====================================================
-- 第七部分：初始怪物数据
-- =====================================================

INSERT INTO `monster` (`name`, `level`, `health`, `max_health`, `attack`, `defense`, `spiritual_power`, `experience_reward`, `scene_code`) VALUES
('青狼', 1, 80, 80, 15, 5, 20, 10, 'village'),
('妖虎', 5, 200, 200, 30, 10, 50, 50, 'mountain'),
('守护兽', 10, 500, 500, 50, 20, 100, 100, 'cave'),
('魔修弟子', 15, 800, 800, 80, 30, 150, 200, 'ruins');

-- =====================================================
-- 第八部分：初始技能数据
-- =====================================================

INSERT INTO `player_skill` (`player_id`, `skill_name`, `skill_level`, `skill_type`, `damage`, `cost`, `cooldown`, `description`) VALUES
('default', '基础吐纳', 1, 'passive', 0, 0, 0, '修仙基础功法，缓慢恢复灵力'),
('default', '火球术', 1, 'active', 30, 10, 5, '基础攻击法术，对敌人造成伤害');

-- =====================================================
-- 第九部分：初始任务数据
-- =====================================================

INSERT INTO `quest` (`quest_code`, `title`, `description`, `type`, `level_requirement`, `objectives`, `rewards`, `experience_reward`, `spirit_stones_reward`) VALUES
('quest_001', '初入仙途', '与村长对话，了解修仙的基础知识', 'main', 0, '[{"type": "dialogue", "target": "elder", "count": 1}]', '["获得：修仙入门知识"]', 50, 10),
('quest_002', '灵根测试', '参加灵根测试', 'main', 0, '[{"type": "story", "target": "node_1_2", "count": 1}]', '["获得：灵根测试结果"]', 100, 20),
('quest_003', '击败青狼', '在村庄附近击败一只青狼', 'side', 1, '[{"type": "combat", "target": "青狼", "count": 1}]', '["获得：灵草 x2", "灵石 x10"]', 30, 15),
('quest_004', '入门任务', '采集灵药', 'main', 5, '[{"type": "explore", "target": "后山", "count": 1}]', '["门派贡献+100", "灵石+50"]', 150, 50),
('quest_005', '宗门比试', '参加入门弟子比试', 'main', 10, '[{"type": "combat", "target": "比试", "count": 1}]', '["中级功法", "经验+200"]', 300, 100);

-- =====================================================
-- 第十部分：初始成就数据
-- =====================================================

INSERT INTO `achievement` (`achievement_code`, `title`, `description`, `category`, `condition_type`, `condition_value`, `experience_reward`, `spirit_stones_reward`) VALUES
('ach_001', '初出茅庐', '第一次与NPC对话', 'social', 'dialogue', '{"count": 1}', 20, 5),
('ach_002', '猎手', '击败10只怪物', 'combat', 'kill_monster', '{"count": 10}', 100, 50),
('ach_003', '探险家', '探索5个不同的场景', 'exploration', 'explore_scene', '{"count": 5}', 150, 75),
('ach_004', '修仙入门', '达到炼气期一层', 'story', 'reach_level', '{"cultivation_level": 1}', 200, 100),
('ach_005', '五行灵根', '被测出拥有五行灵根', 'story', 'story_node', '{"node_id": "node_1_2"}', 300, 150);

-- =====================================================
-- 第十一部分：剧情节点数据（15个节点，约60分钟游戏时长）
-- =====================================================

-- 第一章：平凡少年（节点1-1 到 1-3）
INSERT INTO `story_node` (`node_id`, `npc_code`, `scene_code`, `title`, `description`, `content`, `npc_reply_template`, `keywords`, `rewards`, `next_node_id`, `prerequisite_node_id`, `is_branch`, `is_initial`, `enabled`) VALUES
('node_1_1', 'elder', 'village', '初入青牛镇', '玩家第一次来到青牛镇，遇到村长', '你是一个平凡的少年，听说青牛镇即将举行灵根测试，于是不远万里来到这里。刚进入镇子，你就看到一位慈眉善目的老者站在镇口。', '（慈祥地看着你）年轻人有志向，修仙之路虽艰险，但若心诚，必有所成。', '["修仙", "拜师", "青牛镇"]', '["修仙入门知识"]', 'node_1_2', NULL, FALSE, TRUE, TRUE),

('node_1_2', 'elder', 'village', '灵根测试', '村长带领玩家进行灵根测试', '老者见你风尘仆仆，便主动上前询问。当他得知你是来参加灵根测试的，眼中闪过一丝惊讶。"年轻人，灵根测试可不是闹着玩的，只有真正有资质的人才能踏上修仙之路。"在他的带领下，你来到了测试场地。经过测试，你被测出具有五行灵根，这是非常罕见的资质。', '（对你的资质感到惊讶）五行灵根，万中无一，这可是上天赐予你的礼物。', '["灵根", "测试", "五行灵根"]', '["灵根测试结果"]', 'node_1_3', 'node_1_1', FALSE, FALSE, TRUE),

('node_1_3', 'elder', 'village', '修仙启蒙', '村长教导玩家修仙基础知识', '老者对你的资质非常满意，决定收你为徒，教导你修仙的基础知识。"五行灵根虽然罕见，但也意味着你需要付出更多的努力。我这里有一本《基础吐纳法》，你先好好修炼。"他还告诉你，青云山的掌门即将来镇上挑选弟子，这是你进入大门派的好机会。', '（赠送你一本功法）这本《基础吐纳法》是我当年修行的基础，如今赠予你，望你好好研读。', '["修炼", "吐纳法", "青云山"]', '["基础吐纳法"]', 'node_2_1', 'node_1_2', FALSE, FALSE, TRUE),

-- 第二章：踏上仙途（节点2-1 到 2-3）
('node_2_1', 'elder', 'village', '前往青云山', '玩家前往青云山拜师', '在村长的鼓励下，你决定前往青云山拜师。途中，你遇到了一只小妖兽的袭击。虽然是第一次战斗，但凭借着村长教你的基础功法，你勉强战胜了它。战斗结束后，你感到自己的修为有所提升，同时也更加坚定了修仙的决心。', '（目送你离开）青云山之路充满艰险，望你一路保重。', '["青云山", "拜师", "小妖兽"]', '["经验值+50", "灵石+20"]', 'node_2_2', 'node_1_3', FALSE, FALSE, TRUE),

('node_2_2', 'sectMaster', 'mountain', '拜见掌门', '玩家拜见青云门掌门', '经过长途跋涉，你终于来到了青云山。掌门亲自接见了你，对你的五行灵根非常感兴趣。"五行灵根，万中无一。但修仙之路充满艰辛，你可做好了准备？"在通过掌门的考验后，你正式成为了青云门的入门弟子。', '（满意地点头）不错，五行灵根确实罕见。从今日起，你就是我青云门的弟子了。', '["掌门", "考验", "入门"]', '["成为入门弟子"]', 'node_2_3', 'node_2_1', FALSE, FALSE, TRUE),

('node_2_3', 'sectMaster', 'mountain', '入门任务', '掌门指派第一个任务', '作为入门弟子，你接到了第一个任务：前往后山采集灵药。在采集过程中，你遇到了各种奇妙的植物和小动物，也学到了许多关于灵药的知识。完成任务后，你获得了门派贡献和一些基础修炼资源。', '（交给你一个任务）这是你的第一个任务，完成后会有奖励。', '["任务", "灵药", "后山"]', '["门派贡献+100", "灵石+50"]', 'node_3_1', 'node_2_2', FALSE, FALSE, TRUE),

-- 第三章：修行之路（节点3-1 到 3-3）
('node_3_1', 'seniorBrother', 'mountain', '日常修行', '玩家开始日常修行', '在青云山的日子里，你开始了规律的修行生活。师兄师姐们对你很照顾，经常分享修行心得。"修行如逆水行舟，不进则退。每天的基础修炼虽然枯燥，但却是提升实力的关键。"在他们的指导下，你的修为稳步提升。', '（热情地招呼你）师弟，来，我教你一些修行的技巧。', '["修行", "师兄", "日常"]', '["修为+10"]', 'node_3_2', 'node_2_3', FALSE, FALSE, TRUE),

('node_3_2', 'seniorBrother', 'mountain', '宗门比试', '门派举行入门弟子比试', '门派举行了入门弟子比试，你报名参加了。在比试中，你凭借着五行灵根的优势和平时的努力，取得了不错的成绩。掌门对你的表现非常满意，奖励了你一本中级功法和一些修炼资源。', '（为你鼓掌）好！你的表现非常出色，这是你应得的奖励。', '["比试", "宗门", "奖励"]', '["中级功法", "经验+200"]', 'node_3_3', 'node_3_1', FALSE, FALSE, TRUE),

('node_3_3', 'seniorBrother', 'cave', '神秘洞穴', '玩家探索神秘洞穴', '师兄告诉你后山有一个神秘洞穴，据说里面有古代修士的遗迹。好奇之下，你决定前往探索。在洞穴中，你遇到了一些机关和小怪物，但都被你一一克服。最终，你找到了古代修士的传承，获得了一件宝物和一些高级功法。', '（神秘地告诉你）后山有个好地方，或许会有意外收获。', '["洞穴", "遗迹", "传承"]', '["宝物", "高级功法"]', 'node_4_1', 'node_3_2', FALSE, FALSE, TRUE),

-- 第四章：危机降临（节点4-1 到 4-3）
('node_4_1', 'sectMaster', 'mountain', '魔修入侵', '魔修势力入侵青云山', '就在你沉浸在修行中的时候，魔修势力突然入侵青云山。作为门派弟子，你义不容辞地加入了保卫宗门的战斗。在战斗中，你展现出了惊人的实力，帮助门派击退了魔修的进攻。但掌门在战斗中受了重伤。', '（神色凝重）魔修来犯，你速去支援！', '["魔修", "入侵", "战斗"]', '["经验+500", "声望+100"]', 'node_4_2', 'node_3_3', FALSE, FALSE, TRUE),

('node_4_2', 'sectMaster', 'mountain', '掌门嘱托', '掌门将重要任务托付给玩家', '掌门伤势严重，他将一个重要任务托付给了你：前往上古遗迹寻找一件神器，只有这件神器才能彻底击败魔修。"这件任务危险重重，但我相信你有能力完成。记住，神器不仅是力量的象征，更是责任的象征。"', '（郑重地将任务交给你）这件任务关乎门派存亡，望你全力以赴。', '["任务", "神器", "上古遗迹"]', '["主线任务开启"]', 'node_4_3', 'node_4_1', FALSE, FALSE, TRUE),

('node_4_3', 'mysteriousElder', 'cave', '神秘指引', '神秘老者给予玩家指引', '在前往上古遗迹的路上，你遇到了一位神秘老者。他似乎早就知道你的到来，主动给予你指引。"年轻人，你的命运与这件神器紧密相连。记住，真正的力量来自于内心的坚定，而不是外物。"他还给了你一张地图，标记了神器的具体位置。', '（递给你一张地图）这是上古遗迹的地图，照此前行，你必能找到神器。', '["神秘", "指引", "地图"]', '["遗迹地图"]', 'node_5_1', 'node_4_2', FALSE, FALSE, TRUE),

-- 第五章：最终挑战（节点5-1 到 5-3）
('node_5_1', 'mysteriousElder', 'ruins', '遗迹探险', '玩家进入上古遗迹', '根据神秘老者的指引，你来到了上古遗迹。遗迹中机关重重，你需要解开各种谜题才能前进。在探索过程中，你不仅提升了自己的智慧，也更加了解了古代修士的文明。最终，你来到了神器所在的核心区域。', '（声音在耳边响起）你已接近目标，坚持下去！', '["遗迹", "机关", "谜题"]', '["智慧+20"]', 'node_5_2', 'node_4_3', FALSE, FALSE, TRUE),

('node_5_2', 'demonLeader', 'ruins', '最终对决', '玩家与魔修首领战斗', '就在你即将拿到神器的时候，魔修首领突然出现。他早就跟踪你来到了这里，想要抢夺神器。"哈哈哈，小子，你以为你能阻止我吗？神器是属于我的！"一场激烈的战斗展开了。凭借着你这些天的修行和古代修士的传承，你最终战胜了魔修首领。', '（狰狞地大笑）神器是我的！谁也别想抢走！', '["战斗", "魔修", "神器"]', '["神器", "经验+1000"]', 'node_5_3', 'node_5_1', FALSE, FALSE, TRUE),

('node_5_3', 'sectMaster', 'mountain', '回归宗门', '玩家带着神器返回宗门', '你带着神器返回了青云山。掌门看到你平安归来，非常欣慰。"你不仅完成了任务，更重要的是你展现了一个修仙者应有的品质。从今天起，你就是我青云门的核心弟子。"在掌门的主持下，你正式成为了青云门的核心弟子，开启了新的修仙之路。', '（欣慰地点头）你做得很好，不负众望。从今往后，你就是我青云门的骄傲！', '["回归", "核心弟子", "修仙之路"]', '["成为核心弟子", "开启新篇章"]', NULL, 'node_5_2', FALSE, FALSE, TRUE);

-- =====================================================
-- 第十二部分：对话选项数据（47个选项）
-- =====================================================

-- 节点1-1的选项
INSERT INTO `dialogue_option` (`option_id`, `text`, `npc_code`, `node_id`, `next_node_id`, `response_template`, `order_index`, `enabled`) VALUES
('option_1_1_1', '请问灵根测试什么时候开始？', 'elder', 'node_1_1', 'node_1_2', '灵根测试将在三天后举行，到时候镇上会热闹起来。', 1, TRUE),
('option_1_1_2', '修仙到底是什么样的？', 'elder', 'node_1_1', 'node_1_2', '修仙是一条漫长而艰辛的道路，需要天赋、努力和机缘。但一旦踏上这条路，你将获得常人无法想象的力量。', 2, TRUE),
('option_1_1_3', '镇上最近有什么新鲜事吗？', 'elder', 'node_1_1', 'node_1_2', '除了即将举行的灵根测试，最近镇上来了一位青云门的修士，说是要挑选有资质的弟子。', 3, TRUE),

-- 节点1-2的选项
('option_1_2_1', '我的灵根怎么样？', 'elder', 'node_1_2', 'node_1_3', '你的灵根非常特殊，是五行灵根。这种灵根万中无一，潜力无穷。', 1, TRUE),
('option_1_2_2', '灵根有等级之分吗？', 'elder', 'node_1_2', 'node_1_3', '当然有。灵根分为金、木、水、火、土五行，单一灵根最为纯粹，而五行灵根则最为罕见。', 2, TRUE),
('option_1_2_3', '我该如何开始修炼？', 'elder', 'node_1_2', 'node_1_3', '我这里有一本《基础吐纳法》，你先从基础开始，循序渐进。', 3, TRUE),

-- 节点1-3的选项
('option_1_3_1', '谢谢前辈的指导！', 'elder', 'node_1_3', 'node_2_1', '不必客气，能够遇到你这样有资质的年轻人，是我的荣幸。', 1, TRUE),
('option_1_3_2', '青云山的掌门什么时候来？', 'elder', 'node_1_3', 'node_2_1', '掌门会在灵根测试后到来，到时候你一定要好好表现。', 2, TRUE),
('option_1_3_3', '我需要做什么准备吗？', 'elder', 'node_1_3', 'node_2_1', '好好休息，保持最佳状态。修仙之路，心态比实力更重要。', 3, TRUE),

-- 节点2-2的选项
('option_2_2_1', '我一定会努力修炼的！', 'sectMaster', 'node_2_2', 'node_2_3', '很好，有这份决心，你已经成功了一半。', 1, TRUE),
('option_2_2_2', '门派有什么规矩吗？', 'sectMaster', 'node_2_2', 'node_2_3', '我青云门以正义为本，要求弟子尊师重道，团结互助，不得为非作歹。', 2, TRUE),
('option_2_2_3', '我什么时候能开始正式修炼？', 'sectMaster', 'node_2_2', 'node_2_3', '从明天开始，你将跟随师兄师姐们一起修行。今天先好好休息。', 3, TRUE),

-- 节点2-3的选项
('option_2_3_1', '我一定完成任务！', 'sectMaster', 'node_2_3', 'node_3_1', '好，我相信你。后山的灵药虽然常见，但也要小心不要迷失方向。', 1, TRUE),
('option_2_3_2', '需要采集多少灵药？', 'sectMaster', 'node_2_3', 'node_3_1', '采集十株百年灵芝即可。记住，只取所需，不可过度采摘。', 2, TRUE),
('option_2_3_3', '后山危险吗？', 'sectMaster', 'node_2_3', 'node_3_1', '后山有一些小妖兽，但对你来说应该不成问题。如果遇到危险，及时返回。', 3, TRUE),

-- 节点3-1的选项
('option_3_1_1', '师兄，有什么修行技巧吗？', 'seniorBrother', 'node_3_1', 'node_3_2', '修行没有捷径，只有持之以恒。每天的吐纳练习是基础，千万不能偷懒。', 1, TRUE),
('option_3_1_2', '门派有多少年历史了？', 'seniorBrother', 'node_3_1', 'node_3_2', '青云门已经有千年历史了，是这一带最古老的修仙门派之一。', 2, TRUE),
('option_3_1_3', '我什么时候能进阶？', 'seniorBrother', 'node_3_1', 'node_3_2', '以你的资质，只要努力，三个月内应该可以突破到练气一层。', 3, TRUE),

-- 节点3-2的选项
('option_3_2_1', '谢谢掌门的奖励！', 'seniorBrother', 'node_3_2', 'node_3_3', '不用谢，这是你应得的。继续努力，你会成为门派的骄傲。', 1, TRUE),
('option_3_2_2', '中级功法该如何修炼？', 'seniorBrother', 'node_3_2', 'node_3_3', '中级功法需要更强的灵力基础，你可以先从基础招式开始，逐渐掌握。', 2, TRUE),
('option_3_2_3', '师兄，你有什么故事吗？', 'seniorBrother', 'node_3_2', 'node_3_3', '哈哈，我的故事可多了。等你有时间，我慢慢讲给你听。', 3, TRUE),

-- 节点3-3的选项
('option_3_3_1', '这个洞穴真神秘！', 'seniorBrother', 'node_3_3', 'node_4_1', '对吧？我第一次来的时候也被吓到了。不过里面的宝藏确实值得冒险。', 1, TRUE),
('option_3_3_2', '古代修士真厉害！', 'seniorBrother', 'node_3_3', 'node_4_1', '那是自然，古代修士的智慧和实力远在我们之上。我们要学习的东西还很多。', 2, TRUE),
('option_3_3_3', '我感觉自己变强了！', 'seniorBrother', 'node_3_3', 'node_4_1', '这是好事，但不要骄傲。修仙之路，一山还有一山高。', 3, TRUE),

-- 节点4-1的选项
('option_4_1_1', '掌门，您没事吧？', 'sectMaster', 'node_4_1', 'node_4_2', '我没事，只是受了点伤。魔修的实力比我们想象的要强大。', 1, TRUE),
('option_4_1_2', '我们能战胜魔修吗？', 'sectMaster', 'node_4_1', 'node_4_2', '只要我们团结一心，一定能战胜魔修。但我们需要一件神器的帮助。', 2, TRUE),
('option_4_1_3', '我该怎么做？', 'sectMaster', 'node_4_1', 'node_4_2', '我需要你前往上古遗迹寻找一件神器，只有它才能彻底击败魔修。', 3, TRUE),

-- 节点4-2的选项
('option_4_2_1', '我接受这个任务！', 'sectMaster', 'node_4_2', 'node_4_3', '很好，我相信你。神器就在上古遗迹的核心区域，你一定要小心。', 1, TRUE),
('option_4_2_2', '神器有什么作用？', 'sectMaster', 'node_4_2', 'node_4_3', '这件神器是上古修士留下的，具有净化魔气的力量。有了它，我们就能彻底清除魔修的威胁。', 2, TRUE),
('option_4_2_3', '我需要带什么东西？', 'sectMaster', 'node_4_2', 'node_4_3', '带上你的武器和丹药，遗迹中危险重重。记住，安全第一。', 3, TRUE),

-- 节点4-3的选项
('option_4_3_1', '前辈，您是谁？', 'mysteriousElder', 'node_4_3', 'node_5_1', '我只是一个普通的修士，偶然路过这里。我们有缘，所以我才会帮你。', 1, TRUE),
('option_4_3_2', '我该如何找到神器？', 'mysteriousElder', 'node_4_3', 'node_5_1', '根据地图所指，神器就在遗迹的最深处。那里机关重重，你需要小心应对。', 2, TRUE),
('option_4_3_3', '我一定会成功的！', 'mysteriousElder', 'node_4_3', 'node_5_1', '很好，有这份信心，你就已经迈出了成功的第一步。', 3, TRUE),

-- 节点5-1的选项
('option_5_1_1', '这些机关真复杂', 'mysteriousElder', 'node_5_1', 'node_5_2', '古代修士的智慧令人惊叹，你需要静下心来才能解开这些机关。', 1, TRUE),
('option_5_1_2', '我快接近神器了', 'mysteriousElder', 'node_5_1', 'node_5_2', '很好，但不要掉以轻心。越接近目标，危险越大。', 2, TRUE),
('option_5_1_3', '这里有股强大的力量', 'mysteriousElder', 'node_5_1', 'node_5_2', '那就是神器的气息，它在呼唤你。', 3, TRUE),

-- 节点5-2的选项
('option_5_2_1', '我不会让你得逞的！', 'demonLeader', 'node_5_2', 'node_5_3', '哼，就凭你？可笑！', 1, TRUE),
('option_5_2_2', '为了青云门！', 'demonLeader', 'node_5_2', 'node_5_3', '青云门？不过是蝼蚁罢了！', 2, TRUE),
('option_5_2_3', '神器是我的了！', 'demonLeader', 'node_5_2', 'node_5_3', '可恶！你这个小子！', 3, TRUE),

-- 节点5-3的选项
('option_5_3_1', '谢谢掌门的认可！', 'sectMaster', 'node_5_3', NULL, '这是你应得的，你是我见过最优秀的弟子之一。', 1, TRUE),
('option_5_3_2', '我会继续努力的！', 'sectMaster', 'node_5_3', NULL, '好，修仙之路永无止境，望你继续前行。', 2, TRUE),
('option_5_3_3', '新的冒险在等待着我！', 'sectMaster', 'node_5_3', NULL, '没错，你的修仙之路才刚刚开始。', 3, TRUE);

-- =====================================================
-- 第十三部分：初始玩家数据
-- =====================================================

INSERT INTO `player` (`player_id`, `name`, `level`, `cultivation_level`, `realm`, `current_scene`) VALUES
('player_001', '新弟子', 1, 0, '凡人', 'village');

INSERT INTO `player_state` (`player_id`, `current_scene`, `current_level`, `cultivation_level`, `experience`) VALUES
('player_001', 'village', 1, 0, 0);

INSERT INTO `player_story_progress` (`player_id`, `node_id`, `completed`, `unlocked`) VALUES
('player_001', 'node_1_1', FALSE, TRUE);
