-- =====================================================
-- 增量更新脚本 - 版本 V3
-- 描述: 添加任务系统和成就系统
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

-- 为玩家表添加成就点数字段
ALTER TABLE `player` 
ADD COLUMN `achievement_points` INT DEFAULT 0 COMMENT '成就点数' AFTER `spirit_stones`;

-- 插入初始任务数据
INSERT INTO `quest` (`quest_code`, `title`, `description`, `type`, `level_requirement`, `objectives`, `rewards`, `experience_reward`, `spirit_stones_reward`) VALUES
('quest_001', '初入仙途', '与老者对话，了解修仙的基础知识', 'main', 0, '[{"type": "dialogue", "target": "elder", "count": 1}]', '["获得：修仙入门知识"]', 50, 10),
('quest_002', '灵根测试', '前往聚灵台进行灵根测试', 'main', 0, '[{"type": "explore", "target": "聚灵台", "count": 1}]', '["获得：灵根测试结果"]', 100, 20),
('quest_003', '击败青狼', '在村庄附近击败一只青狼', 'side', 1, '[{"type": "combat", "target": "青狼", "count": 1}]', '["获得：灵草 x2", "灵石 x10"]', 30, 15);

-- 插入初始成就数据
INSERT INTO `achievement` (`achievement_code`, `title`, `description`, `category`, `condition_type`, `condition_value`, `experience_reward`, `spirit_stones_reward`) VALUES
('ach_001', '初出茅庐', '第一次与NPC对话', 'social', 'dialogue', '{"count": 1}', 20, 5),
('ach_002', '猎手', '击败10只怪物', 'combat', 'kill_monster', '{"count": 10}', 100, 50),
('ach_003', '探险家', '探索5个不同的场景', 'exploration', 'explore_scene', '{"count": 5}', 150, 75),
('ach_004', '修仙入门', '达到炼气期一层', 'story', 'reach_level', '{"cultivation_level": 1}', 200, 100);
